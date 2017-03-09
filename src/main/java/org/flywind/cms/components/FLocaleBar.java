package org.flywind.cms.components;

import java.util.Locale;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Service;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.flywind.cms.base.AppBase;

/**
 * <p>语言却换</p>
 * 
 * @author flywind(飞风)
 * @date 2016年1月16日
 * @网址：http://www.flywind.org
 * @QQ技术群：41138107(人数较多最好先加这个)或33106572
 * @since 1.0
 */
public class FLocaleBar extends AppBase {
	
	@Property
	private String locale;

	@Inject
	@Service("ApplicationDefaults")
	private SymbolProvider symbolProvider;

	public String[] getLocales() {
		String symbol = symbolProvider.valueForSymbol(SymbolConstants.SUPPORTED_LOCALES);
		return symbol.split(",");
	}

	public String getLocaleName() {
		Locale l = toLocale(locale);
		if(l.getDisplayName(l).equalsIgnoreCase("中文 (中国)")){
			return "中文";
		}
		return l.getDisplayName(l);
	}

	public boolean isCurrentLocale() {
		return toLocale(locale).equals(currentLocale);
	}

	void onActionFromSwitch(String shortName) {
		persistentLocale.set(toLocale(shortName));
	}

}
