import time
from enum import Enum

import boto3
import settings


class Recorder:
    class Type(Enum):
        START = "Start Recording"
        STOP = "Stop Recording"
        STREAM_STOP = "Stop Streaming"

    class RecorderError(Exception):
        pass

    def __init__(self, arn: str, recordingConfigurationArn: str=settings.IVS_RECORDING_ARN):
        session = boto3.Session(
            aws_access_key_id=settings.AWS_SERVER_PUBLIC_KEY,
            aws_secret_access_key=settings.AWS_SERVER_SECRET_KEY
        )

        self.s3 = session.client('ivs', region_name='us-east-1')
        self.arn = arn
        self.recordingConfigurationArn = recordingConfigurationArn

    def stop_streaming(self):
        self.s3.stop_stream(
            channelArn=self.arn
        )

    def start_recording(self):
        response = self.s3.update_channel(
            arn=self.arn,
            recordingConfigurationArn=self.recordingConfigurationArn
        )
        return response

    def stop_recording(self):
        response = self.s3.update_channel(
            arn=self.arn,
            recordingConfigurationArn=""
        )
        return response

    def isRecording(self):
        response = self.s3.get_channel(
            arn=self.arn
        )

        return response["channel"]["recordingConfigurationArn"] != ""

    def request_looper(self, type: Type, loop_num, sleep_time: int = 5):
        while loop_num > 0:
            try:
                if type == Recorder.Type.START:
                    return self.start_recording()
                elif type == Recorder.Type.STOP:
                    return self.stop_recording()
                elif type == Recorder.Type.STREAM_STOP:
                    return self.stop_streaming()
                else:
                    print("Recorder Type Error")
                    raise Recorder.RecorderError("Recorder Type Error")
            except:
                print("Failed : " + str(type))
                time.sleep(sleep_time)
                loop_num -= 1
        raise Recorder.RecorderError("Recorder Error : " + str(type))

    def request_single(self, type: Type, ignore_error: bool = True):
        try:
            if type == Recorder.Type.START:
                return self.start_recording()
            elif type == Recorder.Type.STOP:
                return self.stop_recording()
            elif type == Recorder.Type.STREAM_STOP:
                return self.stop_streaming()
            else:
                print("Recorder Type Error")
        except:
            print("Failed : " + str(type))
            if not ignore_error:
                raise Recorder.RecorderError("Recorder Error Could not stop stream")



if __name__ == "__main__":
    arn = "arn:aws:ivs:us-east-1:052524269538:channel/HCBh4loJzOvw"
    # stop_streaming(arn)
    recorder = Recorder(arn)
    recorder.request_single(Recorder.Type.STREAM_STOP)
    print(recorder.request_looper(Recorder.Type.STOP, 5))
# response = s3.create_recording_configuration(
#     destinationConfiguration={
#         's3': {
#             'bucketName': settings.BUCKET
#         }
#     },
#     name='new_string_config'
# )
# print(response["recordingConfiguration"])
