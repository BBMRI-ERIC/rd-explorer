package org.molgenis.data.security.exception;

import org.molgenis.data.security.auth.Group;
import org.molgenis.data.security.auth.User;
import org.molgenis.i18n.CodedRuntimeException;

public class FailedToUpdateMemberException extends CodedRuntimeException
{
	private static final String ERROR_CODE = "DS14";

	private final transient User user;
	private final transient Group group;

	public FailedToUpdateMemberException(Group group, User user)
	{
		super(ERROR_CODE);
		this.user = user;
		this.group = group;
	}

	@Override
	public String getMessage()
	{
		return String.format("user:%s group:%s", user.getUsername(), group.getName());
	}

	@Override
	protected Object[] getLocalizedMessageArguments()
	{
		return new Object[] { user.getUsername(), group.getName() };
	}
}
