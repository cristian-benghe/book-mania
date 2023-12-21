package nl.tudelft.sem.template.example.modules.builders;

import nl.tudelft.sem.template.example.modules.user.BannedType;
import nl.tudelft.sem.template.example.modules.user.DetailType;
import nl.tudelft.sem.template.example.modules.user.EmailType;
import nl.tudelft.sem.template.example.modules.user.FollowingType;
import nl.tudelft.sem.template.example.modules.user.PasswordType;
import nl.tudelft.sem.template.example.modules.user.PrivacyType;
import nl.tudelft.sem.template.example.modules.user.User;
import nl.tudelft.sem.template.example.modules.user.UserEnumType;
import nl.tudelft.sem.template.example.modules.user.UsernameType;


public abstract class AbstractUserBuilder {

    public abstract void setBanned(BannedType bannedType);

    public abstract void setDetails(DetailType detailType);

    public abstract void setEmail(EmailType emailType);

    public abstract void setFollowing(FollowingType followingType);

    public abstract void setPassword(PasswordType passwordType);

    public abstract void setPrivacy(PrivacyType privacyType);

    public abstract void setRole(UserEnumType role);

    public abstract void setUsername(UsernameType usernameType);

    public abstract User build();
}
