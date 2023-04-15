
from mimetypes import MimeTypes
import boto3
import settings

session = boto3.Session(
        aws_access_key_id=settings.AWS_SERVER_PUBLIC_KEY,
        aws_secret_access_key=settings.AWS_SERVER_SECRET_KEY
    )

s3 = session.client('s3')


def pre_signed_url_post(bucket: str, key: str, expire: int = 3600):
    response = s3.generate_presigned_post(
        Bucket=bucket,
        Key=key,
        ExpiresIn=expire,
    )

    return {
        "url": response['url'],
        "key": response['fields']["key"],
        "AWSAccessKeyId": response['fields']["AWSAccessKeyId"],
        "policy": response['fields']["policy"],
        "signature": response['fields']["signature"]
    }


def post(response: dict, data):
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


def pre_signed_url_get(bucket: str, key: str, expire: int):
    mime = MimeTypes()
    content_type = mime.guess_type(key)[0]

    return s3.generate_presigned_url(
        ClientMethod='get_object',
        Params={
            'Bucket': bucket,
            'Key': key,
            'ResponseContentType': content_type
        },
        ExpiresIn=expire
    )


if __name__ == "__main__":

    bucket = "mpc-capstone"
    response = pre_signed_url_post(bucket, "bird_extra.jpg", 3600)
    print(response)
    # data = open("assets/bird-thumbnail.jpg", "rb").read()
    # print(data)
    # post(response, data)
    # url = pre_signed_url_get(bucket, "bird_extra.jpg", 3600)
    # print(url)
