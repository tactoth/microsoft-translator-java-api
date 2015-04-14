/*
 * microsoft-translator-java-api
 * 
 * Copyright 2012 Jonathan Griggs <jonathan.griggs at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.memetix.mst.language;

import com.memetix.mst.MicrosoftTranslatorAPI;

import java.io.Serializable;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Language - an enum of all language codes supported by the Microsoft Translator API
 * 
 * Uses the AJAX Interface V2 - see: http://msdn.microsoft.com/en-us/library/ff512399.aspx
 * 
 * @author Jonathan Griggs <jonathan.griggs at gmail.com>
 */
public class Language implements Serializable {
    private static final Map<String, Language> s_languages = new HashMap<String, Language>();

    // pre-defined languages
    public static final Language AUTO_DETECT = get("");
    public static final Language ARABIC = get("ar");
    public static final Language BULGARIAN = get("bg");
    public static final Language CATALAN = get("ca");
    public static final Language CHINESE_SIMPLIFIED = get("zh-CHS");
    public static final Language CHINESE_TRADITIONAL = get("zh-CHT");
    public static final Language CZECH = get("cs");
    public static final Language DANISH = get("da");
    public static final Language DUTCH = get("nl");
    public static final Language ENGLISH = get("en");
    public static final Language ESTONIAN = get("et");
    public static final Language FINNISH = get("fi");
    public static final Language FRENCH = get("fr");
    public static final Language GERMAN = get("de");
    public static final Language GREEK = get("el");
    public static final Language HAITIAN_CREOLE = get("ht");
    public static final Language HEBREW = get("he");
    public static final Language HINDI = get("hi");
    public static final Language HMONG_DAW = get("mww");
    public static final Language HUNGARIAN = get("hu");
    public static final Language INDONESIAN = get("id");
    public static final Language ITALIAN = get("it");
    public static final Language JAPANESE = get("ja");
    public static final Language KOREAN = get("ko");
    public static final Language LATVIAN = get("lv");
    public static final Language LITHUANIAN = get("lt");
    public static final Language MALAY = get("ms");
    public static final Language NORWEGIAN = get("no");
    public static final Language PERSIAN = get("fa");
    public static final Language POLISH = get("pl");
    public static final Language PORTUGUESE = get("pt");
    public static final Language ROMANIAN = get("ro");
    public static final Language RUSSIAN = get("ru");
    public static final Language SLOVAK = get("sk");
    public static final Language SLOVENIAN = get("sl");
    public static final Language SPANISH = get("es");
    public static final Language SWEDISH = get("sv");
    public static final Language THAI = get("th");
    public static final Language TURKISH = get("tr");
    public static final Language UKRAINIAN = get("uk");
    public static final Language URDU = get("ur");
    public static final Language VIETNAMESE = get("vi");

    private static synchronized Language get(String languageCode) {
        Language language = s_languages.get(languageCode);
        if (language == null) {
            language = new Language(languageCode);
            s_languages.put(languageCode, language);
        }

        return language;
    }

    public static void loadAllAvailableLanguages() throws Exception {
        List<String> codesForTranslation = getLanguageCodesForTranslation();

        synchronized (Language.class) {
            for (String code : codesForTranslation) {
                Language language = get(code);
                System.out.println("Inserted language: " + language);
            }
        }
    }

    public static synchronized Language fromString(final String pLanguage) {
        return s_languages.get(pLanguage);
    }

    public static Language[] values() {
        Collection<Language> values_ = s_languages.values();
        return values_.toArray(new Language[values_.size()]);
    }

    /**
     * Microsoft's String representation of this language.
     */
    private final String language;
        
    /**
     * Internal Localized Name Cache
     */
    private Map<Language,String> localizedCache = new ConcurrentHashMap<Language,String>();
	
	/**
	 * Enum constructor.
	 * @param pLanguage The language identifier.
	 */
	private Language(final String pLanguage) {
		language = pLanguage;
	}

    public String getCode() {
        return language;
    }


    /**
	 * Returns the String representation of this language.
	 * @return The String representation of this language.
	 */
	@Override
	public String toString() {
		return getCode();
	}
        
        public static void setKey(String pKey) {
            LanguageService.setKey(pKey);
        }
        
        public static void setClientId(String pId) {
            LanguageService.setClientId(pId);
        }
        public static void setClientSecret(String pSecret) {
            LanguageService.setClientSecret(pSecret);
        }
        
		/**
		 * getName()
		 * 
		 * Returns the name for this language in the tongue of the specified locale
		 * 
		 * If the name is not cached, then it retrieves the name of ALL languages in this locale.
		 * This is not bad behavior for 2 reasons:
		 * 
		 *      1) We can make a reasonable assumption that the client will request the
		 *         name of another language in the same locale 
		 *      2) The GetLanguageNames service call expects an array and therefore we can 
		 *         retrieve ALL names in the same, single call anyway.
		 * 
		 * @return The String representation of this language's localized Name.
		 */
        public String getName(Language locale) throws Exception {
            String localizedName = null;
            if(this.localizedCache.containsKey(locale)) {
                localizedName = this.localizedCache.get(locale);
            } else {
                if(this==Language.AUTO_DETECT||locale==Language.AUTO_DETECT) {
                    localizedName = "Auto Detect";
                } else {
                    //If not in the cache, pre-load all the Language names for this locale
                    String[] names = LanguageService.execute(Language.values(), locale);
                    int i = 0;
                    for(Language lang : Language.values()) {
                        if(lang!=Language.AUTO_DETECT) {   
                            lang.localizedCache.put(locale,names[i]);
                            i++;
                        }
                    }
                    localizedName = this.localizedCache.get(locale);
                }
            }
            return localizedName;
        }
        
     public static List<String> getLanguageCodesForTranslation() throws Exception {
            String[] codes = GetLanguagesForTranslateService.execute();
            return Arrays.asList(codes);
        }
        
     /**
     * values(Language locale)
     * 
	 * Returns a map of all languages, keyed and sorted by 
     * the localized name in the tongue of the specified locale
     * 
     * It returns a map, sorted alphanumerically by the keys()
     * 
     * Key: The localized language name
     * Value: The Language instance
     *
     * @param locale The Language we should localize the Language names with
	 * @return A Map of all supported languages stored by their localized name.
	 */
        public static Map<String,Language> values(Language locale) throws Exception {
            Map<String,Language>localizedMap = new TreeMap<String,Language>(); 
            for(Language lang : Language.values()) {
                if(lang==Language.AUTO_DETECT)
                    localizedMap.put(Language.AUTO_DETECT.getCode(), lang);
                else
                    localizedMap.put(lang.getName(locale), lang);
            }
            return localizedMap;
        }
        
        // Flushes the localized name cache for this language
        private void flushCache() {
            this.localizedCache.clear();
        }
        
        // Flushes the localized name cache for all languages
        public static void flushNameCache() {
            for(Language lang : Language.values())
                lang.flushCache();
        }
        
      private final static class LanguageService extends MicrosoftTranslatorAPI {
            private static final String SERVICE_URL = "http://api.microsofttranslator.com/V2/Ajax.svc/GetLanguageNames?";
            
        /**
         * Detects the language of a supplied String.
         * 
         * @return A DetectResult object containing the language, confidence and reliability.
         * @throws Exception on error.
         */
        public static String[] execute(final Language[] targets, final Language locale) throws Exception {
                //Run the basic service validations first
                validateServiceState(); 
                String[] localizedNames = new String[0];
                if(locale==Language.AUTO_DETECT) {
                    return localizedNames;
                }
                
                final String targetString = buildStringArrayParam(Language.values());
                
                final URL url = new URL(SERVICE_URL 
                        +(apiKey != null ? PARAM_APP_ID + URLEncoder.encode(apiKey,ENCODING) : "") 
                        +PARAM_LOCALE+URLEncoder.encode(locale.toString(), ENCODING)
                        +PARAM_LANGUAGE_CODES + URLEncoder.encode(targetString, ENCODING));
                localizedNames = retrieveStringArr(url);
                return localizedNames;
        }

    }
        
    private final static class GetLanguagesForTranslateService extends MicrosoftTranslatorAPI {
            private static final String SERVICE_URL = "http://api.microsofttranslator.com/V2/Ajax.svc/GetLanguagesForTranslate?";
            
        /**
         * Detects the language of a supplied String.
         * 
         * @return A DetectResult object containing the language, confidence and reliability.
         * @throws Exception on error.
         */
        public static String[] execute() throws Exception {
                //Run the basic service validations first
                validateServiceState(); 
                String[] codes = new String[0];
                
                final URL url = new URL(SERVICE_URL +(apiKey != null ? PARAM_APP_ID + URLEncoder.encode(apiKey,ENCODING) : ""));
                codes = retrieveStringArr(url);
                return codes;
        }

    }
}
