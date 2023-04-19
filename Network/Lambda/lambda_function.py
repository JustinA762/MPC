import base64
import json
import os
import random

import EmailSender
from Database.Data.Account_has_Hardware import Account_has_Hardware
from Database.MPCDatabase import MPCDatabase
from Database.Data.Recording import Recording
from Database.Data.Account import Account, AccountStatus
from Database.Data.Hardware import Hardware
from Database.Data.Criteria import Criteria
from Database.Data.Notification import Notification
from Database.Data.Resolution import Resolution
from Database.Data.Saving_Policy import Saving_Policy
from Database.Data.Hardware_has_Saving_Policy import Hardware_has_Saving_Policy
from Database.Data.Hardware_has_Notification import Hardware_has_Notification
from Error import Error
from FileRegister import pre_signed_url_post, pre_signed_url_get

from mpc_api import MPC_API
import boto3
import re

try:
    from settings import AWS_SERVER_PUBLIC_KEY, AWS_SERVER_SECRET_KEY, BUCKET
except:

    BUCKET = os.environ['BUCKET']
    AWS_SERVER_SECRET_KEY = os.environ['AWS_SERVER_SECRET_KEY']
    AWS_SERVER_PUBLIC_KEY = os.environ['AWS_SERVER_PUBLIC_KEY']

api = MPC_API()
database = MPCDatabase()


def lambda_handler(event, context):
    """Manages the database queries and speaks to the imported libraries to make things possible"""
    print(event)
    print(context)

    status = 200
    resource = event["resource"].lower()
    httpMethod = event["httpMethod"].lower()

    queryPara = {}
    pathPara = {}

    if "queryStringParameters" in event and event["queryStringParameters"] is not None:
        queryPara = event["queryStringParameters"]

    if "pathParameters" in event and event["pathParameters"] is not None:
        pathPara = event["pathParameters"]

    if "body" in event and event["body"] is not None:
        event["body"] = json.loads(event["body"])

    try:
        if resource in api.handlers:
            return api.handlers[resource][httpMethod](event, pathPara, queryPara)
        else:
            return api.handlers["/"]["get"]({"event": event, "context": str(context)}, pathPara, queryPara)
    except Exception as err:
        status = 500
        data = {"error": str(err)}

    return {
        'statusCode': status,
        'headers': {'Content-Type': 'application/json'},
        'body': json.dumps(data)
    }


def json_payload(body, error=False):
    """If there's an error, return an error, if not, then return the proper status code, headers, and body"""
    if error:
        return {
            'statusCode': 400,
            'headers': {'Content-Type': 'application/json'},
            'body': json.dumps({"body": body})
        }
    return {
        'statusCode': 200,
        'headers': {'Content-Type': 'application/json'},
        'body': json.dumps(body)
    }


def check_email(email):
    """Returns true if the email is in the proper format, returns false if it's not"""
    regex = re.compile(r'([A-Za-z0-9]+[.-_])*[A-Za-z0-9]+@[A-Za-z0-9-]+(\.[A-Z|a-z]{2,})+')
    if re.fullmatch(regex, email):
        return True
    else:
        return False


def check_password(password):
    """Makes sure the password is in the correct format and is at least 8 characters"""
    if re.fullmatch(r"^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", password):
        return True
    else:
        return False


def random_by_hash():
    import hashlib
    import datetime
    c = datetime.datetime.now().strftime('%Y/%m/%d %H:%M:%S.%f')[:-3]
    dat = 'python' + c

    hs = hashlib.sha224(dat.encode()).hexdigest()
    return hs


def get_all(table_class):
    """Gets all specified items in the database"""
    items: list[table_class] = database.get_all(table_class)
    dict_list = table_class.list_object_to_dict_list(items)

    return json_payload(dict_list)


def get_by_id(table_class, pathPara):
    """Gets all information in the database of the specified id"""
    id = pathPara["id"]
    item: table_class = database.get_by_id(table_class, id)
    body = table_class.object_to_dict(item)

    return json_payload(body)


def get_by_name(table_class, pathPara):
    """Gets all information based on the specified name"""
    name = pathPara["name"]
    resolution = database.get_by_name(table_class, name)
    body = table_class.object_to_dict(resolution)

    return json_payload(body)


def delete_by_id(table_class, pathPara):
    """Deletes information based on the specified id"""
    database.delete_by_field(table_class, (table_class.ID, pathPara["id"]))

    return json_payload({})


def delete_by_name(table_class, pathPara):
    """Deletes information from the database based on the specified name"""
    database.delete_by_field(table_class, (table_class.NAME, pathPara["name"]))

    return json_payload({})


def delete_by_hardware_id(table_class, pathPara):
    """Deletes information from the database based on the specified hardware id"""
    database.delete_by_field(table_class, (table_class.HARDWARE_ID, pathPara["hardware_id"]))

    return json_payload({})


def update_by_id(table_class, pathPara, queryPara):
    """Updates the database rows in a table based on the specified id"""
    id = pathPara["id"]
    update_keys = set(table_class.COLUMNS).intersection(queryPara.keys())
    if table_class.ID in update_keys:
        update_keys.remove(table_class.ID)
    database.update_fields(table_class, (table_class.ID, id), [(key, queryPara[key]) for key in update_keys])

    return json_payload({})


@api.handle("/")
def home(event, pathPara, queryPara):
    """Handles query events using the json libraries and returns a labeled array"""
    return {
        'statusCode': 200,
        'headers': {'Content-Type': 'application/json'},
        'body': json.dumps(event)
    }


@api.handle("/", httpMethod=MPC_API.POST)
def home(event, pathPara, queryPara):
    """Handles Query events using the json libraries and returns a labeled array"""

    return {
        'statusCode': 200,
        'headers': {'Content-Type': 'application/json'},
        'body': json.dumps(event)
    }


@api.handle("/account")
def accounts_request(event, pathPara, queryPara):
    """Gets all rows and columns of the Account table"""
    return get_all(Account)


@api.handle("/account/signup", httpMethod=MPC_API.POST)
def account_signup(event, pathPara, queryPara):
    """Handles new accounts from users and makes sure user information is in the correct format"""
    body = event["body"]
    error = []
    if database.verify_name(Account, body[Account.NAME]):
        error.append(Error.NAME_DUPLICATE)
    if database.verify_field(Account, Account.EMAIL, body[Account.EMAIL]):
        error.append(Error.EMAIL_DUPLICATE)
    if not check_email(body["email"]):
        error.append(Error.INVALID_EMAIL_FORMAT)
    if not check_password(body["password"]):
        error.append(Error.PASSWORD_WEAK)

    if len(error) == 0:
        database.insert(Account(body["username"], body["password"], body["email"], timestamp="NOW()"))
        return json_payload({"message": "Account created"})
    return json_payload({"message": "\n".join(error)}, True)


@api.handle("/account/profile", httpMethod=MPC_API.POST)
def account_signup(event, pathPara, queryPara):
    """Handles get user information request from users and makes sure user information is in the correct format"""
    body = event["body"]
    if not database.verify_field(Account, Account.TOKEN, body[Account.TOKEN]):
        json_payload({"message": Error.UNKNOWN_ACCOUNT}, True)

    account: Account = database.get_by_field(Account, Account.TOKEN, body[Account.TOKEN])
    return json_payload({"message": "Account found", Account.NAME: account.username,
                         Account.EMAIL: account.email,
                         Account.STATUS: account.status})


@api.handle("/account/signin", httpMethod=MPC_API.POST)
def account_signin(event, pathPara, queryPara):
    """Handles users signing into their account by verifying their username and password in the database"""
    body: dict = event["body"]

    if database.verify_fields(
            Account, [(Account.NAME, body[Account.NAME]), (Account.PASSWORD, body[Account.PASSWORD])]):
        field = Account.NAME

    elif database.verify_fields(
            Account, [(Account.EMAIL, body[Account.NAME]), (Account.PASSWORD, body[Account.PASSWORD])]):
        field = Account.EMAIL
    else:
        return json_payload({"message": "login failed: " + Error.LOGIN_FAILED}, True)

    timestamp_check = database.varidate_timestamp(Account, field, body[Account.NAME])
    database.update_fields(Account, (field, body[Account.NAME]),
                           [(Account.TOKEN, "md5(ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000))"),
                            (Account.TIMESTAMP, "NOW()")])
    if not timestamp_check:
        return json_payload({"message": "login failed: " + Error.TIMESTAMP_ERROR}, True)

    account: Account = database.get_by_field(Account, field, body[Account.NAME])
    return json_payload({"message": "Signed in to Account",
                         Account.TOKEN: account.token, Account.NAME: account.username, Account.EMAIL: account.email})


@api.handle("/account/reset", httpMethod=MPC_API.POST)
def account_signin(event, pathPara, queryPara):
    """Handles users reset their account by verifying their username in the database"""
    body: dict = event["body"]

    if database.verify_fields(
            Account, [(Account.NAME, body[Account.NAME])]):
        field = Account.NAME

    elif database.verify_fields(
            Account, [(Account.EMAIL, body[Account.NAME])]):
        field = Account.EMAIL
    else:
        return json_payload({"message": "Code sent: "})
    code = str(random.randint(100000, 999999))
    database.update_fields(Account, (field, body[Account.NAME]), [(Account.CODE, code)])
    print(code)
    account: Account = database.get_by_field(Account, field, body[Account.NAME])
    if EmailSender.send(account.email, "[MPC Account] Password reset Code", f"Reset code:  {account.code}"):
        return json_payload({"message": "Code sent"})
    else:
        return json_payload({"message": "Failed to send code"})


@api.handle("/account/code", httpMethod=MPC_API.POST)
def account_signin(event, pathPara, queryPara):
    """Handles users reset their account by verifying their username in the database"""
    body: dict = event["body"]

    if database.verify_fields(Account, [(Account.NAME, body[Account.NAME])]):
        field = Account.NAME

    elif database.verify_fields(Account, [(Account.EMAIL, body[Account.NAME])]):
        field = Account.EMAIL
    else:
        return json_payload({"message": Error.INCORRECT_CODE}, True)

    timestamp_check = database.varidate_timestamp(Account, field, body[Account.NAME])
    database.update_fields(Account, (field, body[Account.NAME]), [(Account.TIMESTAMP, "NOW()")])
    if not timestamp_check:
        return json_payload({"message": "login failed: " + Error.TIMESTAMP_ERROR}, True)

    if database.verify_fields(Account, [(field, body[Account.NAME]), (Account.CODE, body[Account.CODE])]):
        database.update_fields(Account, (field, body[Account.NAME]),
                               [(Account.TOKEN, "md5(ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000))"),
                                (Account.TIMESTAMP, "NOW()")])

        token = database.get_field_by_field(Account, Account.TOKEN, field, body[Account.NAME])
        return json_payload({"message": "Code confirmed", Account.TOKEN: token})
    return json_payload({"message": Error.INCORRECT_CODE}, True)


@api.handle("/account/password", httpMethod=MPC_API.POST)
def account_signin(event, pathPara, queryPara):
    """Handles users reset their account by verifying their username in the database"""
    body: dict = event["body"]

    if database.verify_fields(
            Account, [(Account.NAME, body[Account.NAME]), (Account.CODE, body[Account.CODE]),
                      (Account.TOKEN, body[Account.TOKEN])]
    ):
        field = Account.NAME

    elif database.verify_fields(
            Account, [(Account.EMAIL, body[Account.NAME]), (Account.CODE, body[Account.CODE]),
                      (Account.TOKEN, body[Account.TOKEN])]
    ):
        field = Account.EMAIL
    else:
        return json_payload({"message": Error.UNKNOWN_ACCOUNT}, True)

    timestamp_check = database.varidate_timestamp(Account, field, body[Account.NAME])
    database.update_fields(Account, (field, body[Account.NAME]),
                           [(Account.TOKEN, "md5(ROUND(UNIX_TIMESTAMP(CURTIME(4)) * 1000))"),
                            (Account.PASSWORD, body[Account.PASSWORD]),
                            (Account.CODE, None),
                            (Account.TIMESTAMP, "NOW()")])
    if not timestamp_check:
        return json_payload({"message": "login failed: " + Error.TIMESTAMP_ERROR}, True)

    token = database.get_field_by_field(Account, Account.TOKEN, field, body[Account.NAME])
    return json_payload({"message": "Password reset successful", Account.TOKEN: token})


@api.handle("/account/verify/token", httpMethod=MPC_API.POST)
def account_signin(event, pathPara, queryPara):
    """Handles users reset their account by verifying their username in the database"""
    body: dict = event["body"]

    if database.verify_field(Account, Account.TOKEN, body[Account.TOKEN]):
        return json_payload({"message": "Token Found"})

    return json_payload({"message": Error.TOKEN_NOT_FOUND}, True)


@api.handle("/account/verify/device", httpMethod=MPC_API.POST)
def account_signin(event, pathPara, queryPara):
    """Handles users reset their account by verifying their username in the database"""
    body: dict = event["body"]

    if database.verify_fields_by_joins(Account,
                                       [(Account_has_Hardware, Account_has_Hardware.EXPLICIT_ACCOUNT_ID, Account.EXPLICIT_ID),
                                        (Hardware, Hardware.EXPLICIT_HARDWARE_ID, Account_has_Hardware.EXPLICIT_HARDWARE_ID)],
                                       [(Account.TOKEN, body[Account.TOKEN]),
                                        (Hardware.EXPLICIT_DEVICE_ID, body[Hardware.DEVICE_ID])]):
        return json_payload({"message": "Device Found"})
    return json_payload({"message": Error.DEVICE_NOT_FOUND}, True)




@api.handle("/account", httpMethod=MPC_API.POST)
def account_insert(event, pathPara, queryPara):
    """Inserts new row into the account table which represents a new user"""
    account: Account = Account(queryPara["username"], queryPara["password"], queryPara["email"], "C")
    database.insert(account)
    a: Account = database.get_by_name(Account, queryPara["username"])
    return json_payload({"id": a.account_id, "token": a.token})


@api.handle("/account/{id}")
def account_request_by_id(event, pathPara, queryPara):
    """Gets account based on specified id"""
    return get_by_id(Account, pathPara)


@api.handle("/hardware")
def hardware_request(event, pathPara, queryPara):
    """Gets all rows and columns of the hardware table"""
    return get_all(Hardware)


@api.handle("/hardware", httpMethod=MPC_API.POST)
def hardware_insert(event, pathPara, queryPara):
    """Inserts new rows into the hardware table based on account id"""
    if "account_id" in queryPara:
        hardware = Hardware(queryPara["name"], queryPara["max_resolution"], account_id=queryPara["account_id"])
    else:
        hardware = Hardware(queryPara["name"], queryPara["max_resolution"])
    database.insert(hardware)
    id = database.get_id_by_name(Hardware, queryPara["name"])
    return json_payload({"id": id})


@api.handle("/hardware/all", httpMethod=MPC_API.POST)
def hardware_insert(event, pathPara, queryPara):
    """Inserts new rows into the hardware table based on account id"""
    token = event["body"]["token"]

    hardware = database.get_all_join_fields_by_field(
        Hardware,
        [
            (Account_has_Hardware, Account_has_Hardware.EXPLICIT_HARDWARE_ID, Hardware.EXPLICIT_HARDWARE_ID),
            (Account, Account_has_Hardware.EXPLICIT_ACCOUNT_ID, Account.EXPLICIT_ID)
        ], Account.TOKEN, token)
    return json_payload({"hardware": Hardware.list_object_to_dict_list(hardware)})


@api.handle("/hardware/register", httpMethod=MPC_API.PUT)
def hardware_insert(event, pathPara, queryPara):
    """Inserts new rows into the hardware table based on account id"""
    hash_name = random_by_hash()[:12]
    hardware = Hardware(hash_name, "720p", "test-ivs",
                         "arn:aws:ivs:us-east-1:052524269538:channel/HCBh4loJzOvw",
                         "sk_us-east-1_DdqDOfelQCU9_ofTx6s4yekNFgesMT8eLdWIS9k8zLV",
                         "rtmps://1958e2d97d88.global-contribute.live-video.net:443/app/",
                         "https://1958e2d97d88.us-east-1.playback.live-video.net/api/video/v1/us-east-1.052524269538.channel.HCBh4loJzOvw.m3u8")
    database.insert(hardware)
    inserted_hardware = database.get_by_field(Hardware, Hardware.NAME, hash_name)
    return json_payload({"hardware": Hardware.object_to_dict(inserted_hardware)})


@api.handle("/hardware/{id}")
def hardware_request_by_id(event, pathPara, queryPara):
    """Gets information from the hardware table based on specified id"""
    return get_by_id(Hardware, pathPara)


@api.handle("/hardware/{id}", httpMethod=MPC_API.POST)
def hardware_delete_by_id(event, pathPara, queryPara):
    """Deletes rows from the hardware table of the specified id"""
    return delete_by_id(Hardware, pathPara)


@api.handle("/hardware/{id}", httpMethod=MPC_API.POST)
def hardware_update_by_id(event, pathPara, queryPara):
    """Updates the hardware table based on the specified id"""
    return update_by_id(Hardware, pathPara, queryPara)


@api.handle("/recording")
def recordings_request(event, pathPara, queryPara):
    """Gets recordings from the server and and formats the information from AWS into appropriate variables for processing"""
    recordings: list[Recording] = database.get_all(Recording)
    for rec in recordings:
        bucket = "mpc-capstone"
        rec.url = f"https://{bucket}.s3.amazonaws.com/{rec.file_name}"
        host = event["multiValueHeaders"]["Host"][0]
        stage = event["requestContext"]["stage"]
        path = "storage"
        rec.alt_url = f"https://{host}/{stage}/{path}/{bucket}/{rec.file_name}"

    dict_list = Recording.list_object_to_dict_list(recordings)

    return json_payload(dict_list)


@api.handle("/recording", httpMethod=MPC_API.POST)
def recording_insert(event, pathPara, queryPara):
    """Inserts a recording into the database of the specified account id"""
    recording = Recording(queryPara["file_name"], "CURDATE()", "NOW()",
                          account_id=queryPara["account_id"], hardware_id=queryPara["hardware_id"])
    recording.add_date_timestamp_from_query_para(queryPara)
    database.insert(recording)
    id = database.get_id_by_name(Recording, queryPara["file_name"])
    return json_payload({"id": id})


@api.handle("/recording/{id}")
def recording_request_by_id(event, pathPara, queryPara):
    """Gets recording from AWS and stores it in appropriate variables for processing"""
    id = pathPara["id"]
    recording = database.get_by_id(Recording, id)
    bucket = "mpc-capstone"
    recording.url = f"https://{bucket}.s3.amazonaws.com/{recording.file_name}"
    host = event["multiValueHeaders"]["Host"][0]
    stage = event["requestContext"]["stage"]
    path = "storage"
    recording.alt_url = f"https://{host}/{stage}/{path}/{bucket}/{recording.file_name}"
    body = Recording.object_to_dict(recording)

    return json_payload(body)


@api.handle("/recording/{id}", httpMethod=MPC_API.POST)
def recording_delete_by_id(event, pathPara, queryPara):
    """Deletes recording from table based on specified id"""
    return delete_by_id(Recording, pathPara)


@api.handle("/recording/{id}", httpMethod=MPC_API.POST)
def recording_update_by_id(event, pathPara, queryPara):
    """Updates recording table based on specified id"""
    return update_by_id(Recording, pathPara, queryPara)


@api.handle("/criteria")
def criteria_request(event, pathPara, queryPara):
    """Gets all rows and columns from the Criteria table"""
    return get_all(Criteria)


@api.handle("/criteria", httpMethod=MPC_API.POST)
def criteria_insert(event, pathPara, queryPara):
    """Inserts new rows into the criteria table"""
    criteria = Criteria(queryPara["criteria_type"], queryPara["magnitude"], queryPara["duration"])
    database.insert(criteria)
    return json_payload({})


@api.handle("/criteria/{id}")
def criteria_request_by_id(event, pathPara, queryPara):
    """Gets all information from Criteria table based on specified id"""
    return get_by_id(Criteria, pathPara)


@api.handle("/criteria/{id}", httpMethod=MPC_API.POST)
def criteria_delete_by_id(event, pathPara, queryPara):
    """Deletes rows from the criteria table based on the specified id"""
    return delete_by_id(Criteria, pathPara)


@api.handle("/criteria/{id}", httpMethod=MPC_API.POST)
def criteria_update_by_id(event, pathPara, queryPara):
    """Updates the criteria table rows based on the specified id"""
    return update_by_id(Criteria, pathPara, queryPara)


@api.handle("/notification")
def notification_request(event, pathPara, queryPara):
    """Requests notifications from the hardware based on specified notification id"""
    notifications: list[Notification] = database.get_all(Notification)
    for notification in notifications:
        notification.hardware = database.get_hardware_ids_by_notification_id(Hardware_has_Notification,
                                                                             notification.notification_id)
    dict_list = Notification.list_object_to_dict_list(notifications)

    return json_payload(dict_list)


@api.handle("/notification", httpMethod=MPC_API.POST)
def notification_insert(event, pathPara, queryPara):
    """Inserts rows into the notification table"""
    notification = Notification(queryPara[Notification.TYPE], queryPara[Notification.CRITERIA_ID])
    database.insert(notification)
    # id = database.get_id_by_type(Notification, queryPara["notification_type"])
    id = database.get_max_id(Notification)
    if "hardware_id" in queryPara:
        hardware_notification = Hardware_has_Notification(queryPara[Hardware_has_Notification.HARDWARE_ID], id)
        database.insert(hardware_notification)
    return json_payload({"id": id})


@api.handle("/notification/{id}")
def notification_request_by_id(event, pathPara, queryPara):
    """Gets notification from hardware component based on the notification id"""
    id = pathPara["id"]
    notification = database.get_by_id(Notification, id)
    notification.hardware = database.get_hardware_ids_by_notification_id(Hardware_has_Notification,
                                                                         notification.notification_id)
    body = Notification.object_to_dict(notification)

    return json_payload(body)


@api.handle("/notification/{id}", httpMethod=MPC_API.POST)
def notification_delete_by_id(event, pathPara, queryPara):
    """Deletes notification from the table based on specified id"""
    return delete_by_id(Notification, pathPara)


@api.handle("/notification/{id}", httpMethod=MPC_API.POST)
def notification_update_by_id(event, pathPara, queryPara):
    """Updates notification table with new information"""
    return update_by_id(Notification, pathPara, queryPara)


@api.handle("/notification/{id}/add/{hardware_id}", httpMethod=MPC_API.POST)
def notification_insert_hardware(event, pathPara, queryPara):
    """Adds new notification into the into the system from a hardware component"""
    hardware_notification = Hardware_has_Notification(pathPara[Hardware_has_Notification.HARDWARE_ID], pathPara["id"])
    database.insert(hardware_notification)
    return json_payload({})


@api.handle("/notification/{id}/hardware")
def notification_hardware_request(event, pathPara, queryPara):
    """Gets notification from specified hardware component based on id"""
    data = database.get_all_join_field_by_field(Hardware, Hardware_has_Notification,
                                                Hardware.EXPLICIT_HARDWARE_ID,
                                                Hardware_has_Notification.EXPLICIT_HARDWARE_ID,
                                                Hardware_has_Notification.NOTIFICATION_ID, pathPara["id"])
    return json_payload(Hardware.list_object_to_dict_list(data))


@api.handle("/notification/{id}/hardware/{hardware_id}", httpMethod=MPC_API.POST)
def notification_hardware_delete_by_id(event, pathPara, queryPara):
    """Deletes notification based on id"""
    return delete_by_hardware_id(Hardware_has_Notification, pathPara)


@api.handle("/resolution")
def resolution_request(event, pathPara, queryPara):
    """Gets all rows and columns from the Resolution table"""
    return get_all(Resolution)


@api.handle("/resolution", httpMethod=MPC_API.POST)
def resolution_insert(event, pathPara, queryPara):
    """Inserts new rows into the resolution table"""
    resolution = Resolution(queryPara[Resolution.NAME], queryPara[Resolution.WIDTH], queryPara[Resolution.HEIGHT])
    database.insert(resolution)
    return json_payload({})


@api.handle("/resolution/{name}")
def resolution_request_by_name(event, pathPara, queryPara):
    """Gets rows from Resolution table based on name"""
    return get_by_name(Resolution, pathPara)


@api.handle("/resolution/{name}", httpMethod=MPC_API.POST)
def resolution_delete_by_id(event, pathPara, queryPara):
    """Deletes rows from Resolution table based on id"""
    return delete_by_name(Resolution, pathPara)


@api.handle("/resolution/{name}", httpMethod=MPC_API.POST)
def resolution_update_by_id(event, pathPara, queryPara):
    """Updates the resolution table based on specified id"""
    return update_by_id(Notification, pathPara, queryPara)


@api.handle("/saving_policy")
def saving_policy_request(event, pathPara, queryPara):
    """Gets saving policy based on saving policy id"""
    saving_policies = database.get_all(Saving_Policy)
    for policy in saving_policies:
        policy.hardware = database.get_hardware_ids_by_saving_policy_id(Hardware_has_Saving_Policy,
                                                                        policy.saving_policy_id)
    dict_list = Saving_Policy.list_object_to_dict_list(saving_policies)

    return json_payload(dict_list)


@api.handle("/saving_policy", httpMethod=MPC_API.POST)
def saving_policy_insert(event, pathPara, queryPara):
    """Inserts new saving policy into saving_policy table"""
    saving_policy = Saving_Policy(queryPara[Saving_Policy.MAX_TIME], queryPara[Saving_Policy.RESOLUTION_NAME])
    database.insert(saving_policy)
    return json_payload({})


@api.handle("/saving_policy/{id}")
def saving_policy_request_by_id(event, pathPara, queryPara):
    """Gets saving policy based on specified id"""
    id = pathPara["id"]
    saving_policy = database.get_by_id(Saving_Policy, id)
    saving_policy.hardware = database.get_hardware_ids_by_saving_policy_id(Hardware_has_Saving_Policy,
                                                                           saving_policy.saving_policy_id)
    body = Saving_Policy.object_to_dict(saving_policy)

    return json_payload(body)


@api.handle("/saving_policy/{id}", httpMethod=MPC_API.POST)
def saving_policy_delete_by_id(event, pathPara, queryPara):
    """Deletes saving policy based on specified id"""
    return delete_by_id(Saving_Policy, pathPara)


@api.handle("/saving_policy/{id}", httpMethod=MPC_API.POST)
def saving_policy_update_by_id(event, pathPara, queryPara):
    """Updates saving policy table based on id"""
    return update_by_id(Saving_Policy, pathPara, queryPara)


@api.handle("/saving_policy/{id}/add/{hardware_id}", httpMethod=MPC_API.POST)
def saving_policy_add_hardware(event, pathPara, queryPara):
    """Adds hardware component to the saving policy table based on hardware id"""
    saving_policy = Hardware_has_Saving_Policy(pathPara["hardware_id"], pathPara["id"])
    database.insert(saving_policy)
    return json_payload({})


@api.handle("/saving_policy/{id}/hardware")
def saving_policy_hardware_request(event, pathPara, queryPara):
    """Gets information from saving policy and hardware table with a join"""
    data = database.get_all_join_field_by_field(Hardware, Hardware_has_Saving_Policy,
                                                Hardware.EXPLICIT_HARDWARE_ID,
                                                Hardware_has_Saving_Policy.EXPLICIT_HARDWARE_ID,
                                                Hardware_has_Saving_Policy.SAVING_POLICY_ID, pathPara["id"])
    return json_payload(Hardware.list_object_to_dict_list(data))


@api.handle("/saving_policy/{id}/hardware/{hardware_id}", httpMethod=MPC_API.POST)
def saving_policy_hardware_delete_by_id(event, pathPara, queryPara):
    """Deletes saving policy based on the specified hardware id"""
    return delete_by_hardware_id(Hardware_has_Saving_Policy, pathPara)


@api.handle("/email", httpMethod=MPC_API.POST)
def send_email(event, pathPara, queryPara):
    request_body = event["body"]
    return EmailSender.send(request_body["ToMail"], request_body["Subject"], request_body["LetterBody"])


@api.handle("/file/all", httpMethod=MPC_API.POST)
def upload_url(event, pathPara, queryPara):
    token = event["body"]["token"]
    if not database.verify_field(Account, Account.TOKEN, token):
        return json_payload({"message": "Account does not exist"})
    recordings: list[Recording] = database.get_all_join_field_by_field(Recording, Account,
                                                      Account.EXPLICIT_ID, Recording.EXPLICIT_ACCOUNT_ID,
                                                      Account.TOKEN, token)
    for recording in recordings:
        recording.url = pre_signed_url_get(BUCKET, recording.file_name, 3600)
    return json_payload({
        "files": Hardware.list_object_to_dict_list(recordings)
    })


# @api.handle("/file/add", httpMethod=MPC_API.POST)
# def upload_url(event, pathPara, queryPara):
#     id = event["body"]["token"]
#
#     account_id = database.get_field_by_field(Account, Account.ID, Account.TOKEN, token)
#     recording = Recording(queryPara["file_name"], "CURDATE()", "NOW()",
#                           account_id=account_id, hardware_id=queryPara["hardware_id"])
#     recording.add_date_timestamp_from_query_para(queryPara)
#     database.insert(recording)
#     id = database.get_id_by_name(Recording, queryPara["file_name"])
#     return json_payload({"id": id})


@api.handle("/file/upload-url/{key}")
def upload_url(event, pathPara, queryPara):
    response = pre_signed_url_post(BUCKET, pathPara["key"], 10)
    return json_payload(response)


@api.handle("/file/download-url/{key}")
def download_url(event, pathPara, queryPara):
    response = pre_signed_url_get(BUCKET, pathPara["key"], 10)
    return json_payload(response)


if __name__ == "__main__":
    # database.insert(Notification(10000, criteria_id=3), ignore=True)
    # event = {
    #     "resource": "/file/{key}/upload-url",
    #     "httpMethod": MPC_API.POST,
    #     "body": """{
    #         "username": "tun05036@temple.edu",
    #         "password": "password",
    #         "email": "default@temple.edu",
    #         "code": "658186"
    #     }""",
    #     "pathParameters": {
    #         "key": "sample.txt"
    #     },
    #     "queryStringParameters": {
    #         "notification_type": 10
    #     }
    # }
    # print(lambda_handler(event, None))

    event = {
        "resource": "/account/verify/device",
        "httpMethod": "POST",
        "body": """{
            "username": "tun05036@temple.edu",
            "password": "password",
            "email": "default@temple.edu",
            "code": "658186",
            "token": "0d94d4bdceedba53f4cccf7cfa3ecc3c",
            "device_id": "5b9ca48d26390983524f551489319af4"
        }""",
        "pathParameters": {
            "token": "c0d12f97a5989f6852603badff33ceb6"
        },
        "queryStringParameters": {
            "notification_type": 10
        }
    }
    print(lambda_handler(event, None))
    # database.delete_by_field(Hardware, (Hardware.ID, "50809c298c5a1a3214b115390b6b725c"))
    database.close()
    # data = database.get_all_join_fields_by_field(Hardware,
    #                                             [
    #                                                 (Account_has_Hardware, Account_has_Hardware.EXPLICIT_HARDWARE_ID, Hardware.EXPLICIT_HARDWARE_ID),
    #                                                 (Account, Account_has_Hardware.EXPLICIT_ACCOUNT_ID, Account.EXPLICIT_ID)
    #                                             ], Account.NAME, "John Smith")
    # for d in data:
    #     print(d)
