/**
 * A simple mixin for attaching javascript that invokes a listener in the component via AJAX.
 * Based on http://tinybits.blogspot.com/2010/03/new-and-better-zoneupdater.html
 * and http://tinybits.blogspot.com/2009/05/simple-onevent-mixin.html.
 */
package org.flywind.cms.mixins;

import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

// The @Import tells Tapestry to put a link to the file in the head of the page so that the browser will pull it in. 
//@Import(library = "AjaxValidator.js")
public class AjaxValidator {

	// Useful bits and pieces

	@Inject
	private ComponentResources componentResources;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	/**
	 * The element we attach ourselves to
	 */
	@InjectContainer
	private ClientElement clientElement;

	// The code

	void afterRender() {

		// Tell the Tapestry.Initializer to do the initializing of an AjaxValidator, which it will do when the DOM has
		// been fully loaded.

		JSONObject spec = new JSONObject();
		spec.put("elementId", clientElement.getClientId());
		spec.put("listenerURI", componentResources.createEventLink("ajaxValidate").toAbsoluteURI());
		//javaScriptSupport.addInitializerCall("ajaxValidator", spec);
		javaScriptSupport.require("init-AjaxValidator").invoke("init").with(spec);
	}
}
