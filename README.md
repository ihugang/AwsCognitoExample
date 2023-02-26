# AwsCognitoExample
Example usage of AWS Cognito SDK for SignUp, ConfirmSignUp, and Login:

SignUp: Use the user's email address as the username, and optionally provide a nickname as the display name.

ConfirmSignUp: Once the user has signed up, they will receive a confirmation code via email. The code should be entered to confirm the account.

Login: After successful confirmation, the user can login using their email address and password. Once authenticated, the access token should be saved for subsequent API calls.
