package org.molgenis.api.identities;

import com.google.auto.value.AutoValue;
import org.molgenis.data.security.auth.RoleMembership;

@AutoValue
@SuppressWarnings("java:S1610")
public abstract class GroupMemberResponse {
  public abstract UserResponse getUser();

  public abstract RoleResponse getRole();

  static GroupMemberResponse fromEntity(RoleMembership role) {
    return new AutoValue_GroupMemberResponse(
        UserResponse.fromEntity(role.getUser()), RoleResponse.fromEntity(role.getRole()));
  }
}
