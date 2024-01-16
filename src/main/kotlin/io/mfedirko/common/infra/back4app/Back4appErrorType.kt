package io.mfedirko.common.infra.back4app

enum class Back4appErrorType(val code: Int, val description: String) {
//    API errors
    UserInvalidLoginParams(101, "Invalid login parameters. Check error message for more details."),
    ObjectNotFound(101, "The specified object or session doesn’t exist or could not be found. Can also indicate that you do not have the necessary permissions to read or write this object. Check error message for more details."),
    InvalidQuery(102, "There is a problem with the parameters used to construct this query. This could be an invalid field name or an invalid field type for a specific constraint. Check error message for more details."),
    InvalidFieldName(105, "An invalid field name. Keys are case-sensitive. They must start with a letter, and a-zA-Z0-9_ are the only valid characters. Some field names may be reserved. Check error message for more details."),
    InvalidJSON(107, "Badly formed JSON was received upstream. This either indicates you have done something unusual with modifying how things encode to JSON, or the network is failing badly. Can also indicate an invalid utf-8 string or use of multiple form encoded values. Check error message for more details."),
    NotInitialized(109, "You must call Parse.initialize before using the Parse library. Check the Quick Start guide for your platform."),
    ObjectTooLarge(116, "The object is too large. Parse Objects have a max size of 128 kilobytes."),
    ExceededConfigParamsError(116, "You have reached the limit of 100 config parameters."),
    InvalidLimitError(117, "An invalid value was set for the limit. Check error message for more details."),
    InvalidSkipError(118, "An invalid value was set for skip. Check error message for more details."),
    OperationForbidden(119, "The operation isn’t allowed for clients due to class-level permissions. Check error message for more details."),
    CacheMiss(120, "The result was not found in the cache."),
    InvalidNestedKey(121, "An invalid key was used in a nested JSONObject. Check error message for more details."),
    InvalidACL(123, "An invalid ACL was provided."),
    InvalidEmailAddress(125, "The email address was invalid."),
    DuplicateValue(137, "Unique field was given a value that is already taken."),
    InvalidRoleName(139, "Role’s name is invalid."),
    ReservedValue(139, "Field value is reserved."),
    ExceededCollectionQuota(140, "You have reached the quota on the number of classes in your app. Please delete some classes if you need to add a new class."),
    ScriptFailed(141, "Cloud Code script failed. Usually points to a JavaScript error. Check error message for more details."),
    FunctionNotFound(141, "Cloud function not found. Check that the specified Cloud function is present in your Cloud Code script and has been deployed."),
    JobNotFound(141, "Background job not found. Check that the specified job is present in your Cloud Code script and has been deployed."),
    SuccessErrorNotCalled(141, "success/error was not called. A cloud function will return once response.success() or response.error() is called. A background job will similarly finish execution once status.success() or status.error() is called. If a function or job never reaches either of the success/error methods, this error will be returned. This may happen when a function does not handle an error response correctly, preventing code execution from reaching the success() method call."),
    MultupleSuccessErrorCalls(141, "Can’t call success/error multiple times. A cloud function will return once response.success() or response.error() is called. A background job will similarly finish execution once status.success() or status.error() is called. If a function or job calls success() and/or error() more than once in a single execution path, this error will be returned."),
    ValidationFailed(142, "Cloud Code validation failed."),
    WebhookError(143, "Webhook error."),
    InvalidImageData(150, "Invalid image data."),
    UnsavedFileError(151, "An unsaved file."),
    InvalidPushTimeError(152, "An invalid push time was specified."),
    HostingError(158, "Hosting error."),
    InvalidEventName(160, "The provided analytics event name is invalid."),
    ClassNotEmpty(255, "Class is not empty and cannot be dropped."),
    AppNameInvalid(256, "App name is invalid."),
    MissingAPIKeyError(902, "The request is missing an API key."),
    InvalidAPIKeyError(903, "The request is using an invalid API key."),
//    User related errors
    UsernameMissing(200, "Invalid login parameters. Check error message for more details."),
    PasswordMissing(201, "The specified object or session doesn’t exist or could not be found. Can also indicate that you do not have the necessary permissions to read or write this object. Check error message for more details."),
    UsernameTaken(202, "There is a problem with the parameters used to construct this query. This could be an invalid field name or an invalid field type for a specific constraint. Check error message for more details."),
    UserEmailTaken(203, "An invalid field name. Keys are case-sensitive. They must start with a letter, and a-zA-Z0-9_ are the only valid characters. Some field names may be reserved. Check error message for more details."),
    UserEmailMissing(204, "Badly formed JSON was received upstream. This either indicates you have done something unusual with modifying how things encode to JSON, or the network is failing badly. Can also indicate an invalid utf-8 string or use of multiple form encoded values. Check error message for more details."),
    UserWithEmailNotFound(205, "You must call Parse.initialize before using the Parse library. Check the Quick Start guide for your platform."),
    SessionMissing(206, "A user object without a valid session could not be altered."),
    MustCreateUserThroughSignup(207, "A user can only be created through signup."),
    AccountAlreadyLinked(208, "An account being linked is already linked to another user."),
    InvalidSessionToken(209, "The device’s session token is no longer valid. The application should ask the user to log in again."),
//    General Issues
    OtherCause(-1, "Invalid login parameters. Check error message for more details."),
    InternalServerError(1, "The specified object or session doesn’t exist or could not be found. Can also indicate that you do not have the necessary permissions to read or write this object. Check error message for more details."),
    ServiceUnavailable(2, "There is a problem with the parameters used to construct this query. This could be an invalid field name or an invalid field type for a specific constraint. Check error message for more details."),
    ClientDisconnected(4, "An invalid field name. Keys are case-sensitive. They must start with a letter, and a-zA-Z0-9_ are the only valid characters. Some field names may be reserved. Check error message for more details");

    override fun toString(): String {
        return "$name($code: $description)"
    }

    companion object {
        fun fromCode(code: Int): Back4appErrorType? {
            return values().firstOrNull { it.code == code }
        }
    }
}