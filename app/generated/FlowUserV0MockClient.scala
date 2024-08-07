/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.18.69
 * User agent: apibuilder app.apibuilder.io/flow/user/latest/play_2_8_mock_client
 */
package io.flow.user.v0.mock {

  trait Client extends io.flow.user.v0.interfaces.Client {

    val baseUrl: String = "http://mock.localhost"

    override def emailVerifications: io.flow.user.v0.EmailVerifications = MockEmailVerificationsImpl
    override def users: io.flow.user.v0.Users = MockUsersImpl
    override def passwordResetForms: io.flow.user.v0.PasswordResetForms = MockPasswordResetFormsImpl

  }

  object MockEmailVerificationsImpl extends MockEmailVerifications

  trait MockEmailVerifications extends io.flow.user.v0.EmailVerifications {

    /**
     * @param token The unique token sent to the user to verify their email address
     */
    def postByToken(
      token: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.user.v0.models.EmailVerification] = scala.concurrent.Future.successful {
      io.flow.user.v0.mock.Factories.makeEmailVerification()
    }

  }

  object MockUsersImpl extends MockUsers

  trait MockUsers extends io.flow.user.v0.Users {

    /**
     * Search users. Must specify an id or email.
     *
     * @param email Find users with this email address. Case insensitive. Exact match
     * @param status Find users with this status
     */
    def get(
      id: _root_.scala.Option[Seq[String]] = None,
      email: _root_.scala.Option[String] = None,
      status: _root_.scala.Option[io.flow.common.v0.models.UserStatus] = None,
      limit: Long = 25L,
      offset: Long = 0L,
      sort: String = "-created_at",
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Seq[io.flow.common.v0.models.User]] = scala.concurrent.Future.successful {
      Nil
    }

    /**
     * Authenticates a user by email / password. Note only users that have a status of
     * active will be authorized.
     */
    def postAuthenticate(
      authenticationForm: io.flow.user.v0.models.AuthenticationForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = scala.concurrent.Future.successful {
      io.flow.common.v0.mock.Factories.makeUser()
    }

    /**
     * Returns information about a specific user.
     */
    def getById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = scala.concurrent.Future.successful {
      io.flow.common.v0.mock.Factories.makeUser()
    }

    /**
     * Create a new user. Note that new users will be created with a status of pending
     * and will not be able to authenticate until approved by a member of the Flow
     * team.
     */
    def post(
      userForm: io.flow.user.v0.models.UserForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = scala.concurrent.Future.successful {
      io.flow.common.v0.mock.Factories.makeUser()
    }

    /**
     * Update a user.
     */
    def putById(
      id: String,
      userPutForm: io.flow.user.v0.models.UserPutForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.User] = scala.concurrent.Future.successful {
      io.flow.common.v0.mock.Factories.makeUser()
    }

    /**
     * Update the password for a user.
     */
    def patchPasswordsById(
      id: String,
      passwordChangeForm: io.flow.user.v0.models.PasswordChangeForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = scala.concurrent.Future.successful {
      // unit type
    }

    /**
     * Deletes a password for the given user.
     */
    def deletePasswordsById(
      id: String,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = scala.concurrent.Future.successful {
      // unit type
    }

  }

  object MockPasswordResetFormsImpl extends MockPasswordResetForms

  trait MockPasswordResetForms extends io.flow.user.v0.PasswordResetForms {

    def postResets(
      passwordResetRequestForm: io.flow.user.v0.models.PasswordResetRequestForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[Unit] = scala.concurrent.Future.successful {
      // unit type
    }

    def post(
      passwordResetForm: io.flow.user.v0.models.PasswordResetForm,
      expand: _root_.scala.Option[Seq[String]] = None,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.common.v0.models.ExpandableUser] = scala.concurrent.Future.successful {
      io.flow.common.v0.mock.Factories.makeExpandableUser()
    }

  }

  object Factories {

    def randomString(length: Int = 24): String = {
      _root_.scala.util.Random.alphanumeric.take(length).mkString
    }

    def makeAuthenticationForm(): io.flow.user.v0.models.AuthenticationForm = io.flow.user.v0.models.AuthenticationForm(
      email = Factories.randomString(24),
      password = Factories.randomString(24)
    )

    def makeEmailVerification(): io.flow.user.v0.models.EmailVerification = io.flow.user.v0.models.EmailVerification(
      email = Factories.randomString(24)
    )

    def makeNameForm(): io.flow.user.v0.models.NameForm = io.flow.user.v0.models.NameForm(
      first = None,
      last = None
    )

    def makePasswordChangeForm(): io.flow.user.v0.models.PasswordChangeForm = io.flow.user.v0.models.PasswordChangeForm(
      current = Factories.randomString(24),
      `new` = Factories.randomString(24)
    )

    def makePasswordResetForm(): io.flow.user.v0.models.PasswordResetForm = io.flow.user.v0.models.PasswordResetForm(
      token = Factories.randomString(24),
      password = Factories.randomString(24)
    )

    def makePasswordResetRequestForm(): io.flow.user.v0.models.PasswordResetRequestForm = io.flow.user.v0.models.PasswordResetRequestForm(
      email = Factories.randomString(24)
    )

    def makeUserForm(): io.flow.user.v0.models.UserForm = io.flow.user.v0.models.UserForm(
      email = None,
      password = None,
      name = None
    )

    def makeUserPutForm(): io.flow.user.v0.models.UserPutForm = io.flow.user.v0.models.UserPutForm(
      email = None,
      name = None
    )

    def makeUserVersion(): io.flow.user.v0.models.UserVersion = io.flow.user.v0.models.UserVersion(
      id = Factories.randomString(24),
      timestamp = _root_.org.joda.time.DateTime.now,
      `type` = io.flow.common.v0.mock.Factories.makeChangeType(),
      user = io.flow.common.v0.mock.Factories.makeUser()
    )

  }

}