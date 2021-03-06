package org.molgenis.app.controller;

import org.molgenis.core.ui.controller.AbstractStaticContentController;
import org.molgenis.web.PluginController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** Controller that handles references page requests */
@Controller
@RequestMapping(ReferencesController.URI)
public class ReferencesController extends AbstractStaticContentController {
  public static final String ID = "references";
  public static final String URI = PluginController.PLUGIN_URI_PREFIX + ID;

  public ReferencesController() {

    super(ID, URI);
  }
}
