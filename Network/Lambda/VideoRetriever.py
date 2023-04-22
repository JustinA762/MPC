import os
from mimetypes import MimeTypes

import boto3
from Database.MPCDatabase import MPCDatabase
from settings import AWS_SERVER_SECRET_KEY, AWS_SERVER_PUBLIC_KEY, PREFIX, MEDIACONVERT_ENDPOINT, \
    JOB_TEMPLATE_NAME, MEDIACONVERT_ROLE, CONVERTED


class VideoRetriever:

    def __init__(self, bucket: str):
        session = boto3.Session(
            aws_access_key_id=AWS_SERVER_PUBLIC_KEY,
            aws_secret_access_key=AWS_SERVER_SECRET_KEY
        )

        self.s3 = session.client('s3')
        self.bucket = bucket

    def post(self, response: dict, data):
        import requests
        files = {'file': data}
        r = requests.post(
            response["url"],
            data={
                "key": response["key"],
                "AWSAccessKeyId": response["AWSAccessKeyId"],
                "policy": response["policy"],
                "signature": response["signature"]
            },
            files=files)
        print(r.status_code)

    def pre_signed_url_get(self, key: str, expire: int):
        mime = MimeTypes()
        content_type = mime.guess_type(key)[0]
        if key[-len(".ts"):] == ".ts":
            content_type = "video/MP2T"
        return self.s3.generate_presigned_url(
            ClientMethod='get_object',
            Params={
                'Bucket': self.bucket,
                'Key': key,
                'ResponseContentType': content_type
            },
            ExpiresIn=expire
        )

    def get_all(self, channelArn: str):

        response = self.s3.list_objects(
            Bucket=self.bucket,
            Prefix=f"{PREFIX}{channelArn.split(':')[4]}/{channelArn.split('/')[1]}"
        )
        file_names = []

        for video in response['Contents']:
            splitted_key = video["Key"].split("/")
            if len(splitted_key) > 9 and splitted_key[11] == "hls" and splitted_key[12] == "720p30":
                file_names.append(video["Key"])

        return file_names

    def convert_video(self, title: str, keys: list[str]):
        settings = self.make_settings(title, keys)
        user_metadata = {
            'JobCreatedBy': 'videoConvertSample',
        }

        client = boto3.client('mediaconvert', endpoint_url=MEDIACONVERT_ENDPOINT)
        result = client.create_job(
            Role=MEDIACONVERT_ROLE,
            JobTemplate=JOB_TEMPLATE_NAME,
            # 入力ファイルの情報や、上書きしたいパラメータの情報などを渡す
            Settings=settings,
            # タスクにユーザ定義のデータを紐付けることもできる。
            # キーと値が両方 `str` でないとダメ。
            UserMetadata=user_metadata,
        )

    def convert_video_in_channel(self, database: MPCDatabase, channelArn, resolution_p: str = "720p", fps: str = "30"):
        account_id =channelArn.split(':')[4]
        stream_id = channelArn.split('/')[1]
        stream_file_prefix = f"{PREFIX}/{account_id}/{stream_id}/"
        converted_file_prefix = f"{CONVERTED}/{stream_id}"
        stream_response = self.s3.list_objects(
            Bucket=self.bucket,
            Prefix=stream_file_prefix
        )

        converted_response = self.s3.list_objects(
            Bucket=self.bucket,
            Prefix=converted_file_prefix
        )

        if "Contents" in stream_response:
            stream_files = [i["Key"] for i in stream_response["Contents"]]
        else:
            stream_files = []

        if "Contents" in converted_response:
            converted_files = [i["Key"] for i in converted_response["Contents"]]
        else:
            converted_files = []

        stream_files_map = {}
        for file in stream_files:
            basename = os.path.basename(file)
            folder_name = file.replace(basename, "")
            if file[-len(".ts"):] != ".ts" or folder_name[-len(f"{resolution_p}{fps}/"):] != f"{resolution_p}{fps}/":
                continue
            folder_name = folder_name\
                .replace(f"/media/hls/{resolution_p}{fps}/", "")\
                .replace(stream_file_prefix, "")\
                .replace("/", "-") + "/"
            if folder_name not in stream_files_map:
                stream_files_map[folder_name] = [file]
            else:
                stream_files_map[folder_name].append(file)
        print(stream_files_map)

    def make_input(self, key):
        return {
            "AudioSelectors": {
                "Audio Selector 1": {
                    "Offset": 0,
                    "DefaultSelection": "DEFAULT",
                    "SelectorType": "LANGUAGE_CODE",
                    "ProgramSelection": 1,
                    "LanguageCode": "ENM"
                }
            },
            "FileInput": f"s3://{self.bucket}/{key}"
        }

    def make_settings(self, title: str, keys: list[str]):

        # APIリファレンスを参考に設定
        return \
            {
                "Inputs": [self.make_input(k) for k in keys],
                "OutputGroups": [
                    {
                        "OutputGroupSettings": {
                            "FileGroupSettings": {
                                # 出力先パス。別バケットも可。トリガーの設定に応じて適宜変更してください。
                                "Destination": f"s3://{self.bucket}/converted/{title}/"
                            }
                        }
                    }
                ],
            }
