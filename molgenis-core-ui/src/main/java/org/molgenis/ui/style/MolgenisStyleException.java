package org.molgenis.ui.style;

import org.molgenis.data.CodedRuntimeException;

import static java.util.Objects.requireNonNull;
import static org.molgenis.data.i18n.LanguageServiceHolder.getLanguageService;

public class MolgenisStyleException extends CodedRuntimeException
{
	private final static String ERROR_CODE = "C05";
	private Throwable cause;

	public MolgenisStyleException(Throwable cause)
	{
		super(ERROR_CODE, cause);
		this.cause = requireNonNull(cause);
	}

	@Override
	public String getMessage()
	{
		return String.format("cause:%s", cause.getMessage());
	}

	@Override
	public String getLocalizedMessage()
	{
		return getLanguageService().map(languageService ->
		{
			String format = languageService.getString(ERROR_CODE);
			return format;
		}).orElseGet(super::getLocalizedMessage);
	}
}
