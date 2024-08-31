package org.futo.inputmethod.keyboard.internal;

import java.util.*;

public class TranslationHelper {
    private static final Map<String, String> LANGUAGES = new HashMap<>();

    static {
        initializeLanguages();
    }

    public static Map<String, String> getLanguages() {
        return LANGUAGES;
    }

    private static void initializeLanguages() {
        LANGUAGES.put("auto", "Auto Detect");
        LANGUAGES.put("af","Afrikaans");
        LANGUAGES.put("sq","Albanian");
        LANGUAGES.put("am","Amharic");
        LANGUAGES.put("ar","Arabic");
        LANGUAGES.put("hy","Armenian");
        LANGUAGES.put("as","Assamese");
        LANGUAGES.put("az","Azerbaijani");
        LANGUAGES.put("bm","Bambara");
        LANGUAGES.put("ba","Bashkir");
        LANGUAGES.put("eu","Basque");
        LANGUAGES.put("be","Belarusian");
        LANGUAGES.put("bn","Bengali");
        LANGUAGES.put("bho","Bhojpuri");
        LANGUAGES.put("bg","Bulgarian");
        LANGUAGES.put("yue","Cantonese");
        LANGUAGES.put("ca","Catalan");
        LANGUAGES.put("ceb","Cebuano");
        LANGUAGES.put("ny","Chichewa");
        LANGUAGES.put("lzh","Chinese (Literary)");
        LANGUAGES.put("zh-CN","Chinese (Simplified)");
        LANGUAGES.put("zh-TW","Chinese (Traditional)");
        LANGUAGES.put("cv","Chuvash");
        LANGUAGES.put("co","Corsican");
        LANGUAGES.put("hr","Croatian");
        LANGUAGES.put("cs","Czech");
        LANGUAGES.put("da","Danish");
        LANGUAGES.put("prs","Dari");
        LANGUAGES.put("dv","Dhivehi");
        LANGUAGES.put("doi","Dogri");
        LANGUAGES.put("mhr","Eastern Mari");
        LANGUAGES.put("en","English");
        LANGUAGES.put("eo","Esperanto");
        LANGUAGES.put("et","Estonian");
        LANGUAGES.put("ee","Ewe");
        LANGUAGES.put("fo","Faroese");
        LANGUAGES.put("fj","Fijian");
        LANGUAGES.put("tl","Filipino");
        LANGUAGES.put("fi","Finnish");
        LANGUAGES.put("fr","French");
        LANGUAGES.put("fr-CA","French (Canadian)");
        LANGUAGES.put("fy","Frisian");
        LANGUAGES.put("gl","Galician");
        LANGUAGES.put("ka","Georgian");
        LANGUAGES.put("de","German");
        LANGUAGES.put("el","Greek");
        LANGUAGES.put("gn","Guarani");
        LANGUAGES.put("gu","Gujarati");
        LANGUAGES.put("ha","Hausa");
        LANGUAGES.put("haw","Hawaiian");
        LANGUAGES.put("he","Hebrew");
        LANGUAGES.put("mrj","Hill Mari");
        LANGUAGES.put("hi","Hindi");
        LANGUAGES.put("hu","Hungarian");
        LANGUAGES.put("is","Icelandic");
        LANGUAGES.put("ig","Igbo");
        LANGUAGES.put("ilo","Ilocano");
        LANGUAGES.put("id","Indonesian");
        LANGUAGES.put("ikt","Inuinnaqtun");
        LANGUAGES.put("iu","Inuktitut");
        LANGUAGES.put("iu-Latn","Inuktitut (Latin)");
        LANGUAGES.put("ga","Irish");
        LANGUAGES.put("it","Italian");
        LANGUAGES.put("ja","Japanese");
        LANGUAGES.put("jv","Javanese");
        LANGUAGES.put("kn","Kannada");
        LANGUAGES.put("kk","Kazakh");
        LANGUAGES.put("km","Khmer");
        LANGUAGES.put("rw","Kinyarwanda");
        LANGUAGES.put("tlh-Latn","Klingon");
        LANGUAGES.put("gom","Konkani");
        LANGUAGES.put("kri","Krio");
        LANGUAGES.put("ckb","Kurdish (Central)");
        LANGUAGES.put("ku","Kurdish (Northern)");
        LANGUAGES.put("ky","Kyrgyz");
        LANGUAGES.put("lo","Lao");
        LANGUAGES.put("la","Latin");
        LANGUAGES.put("lv","Latvian");
        LANGUAGES.put("ln","Lingala");
        LANGUAGES.put("lt","Lithuanian");
        LANGUAGES.put("lg","Luganda");
        LANGUAGES.put("lb","Luxembourgish");
        LANGUAGES.put("mk","Macedonian");
        LANGUAGES.put("mai","Maithili");
        LANGUAGES.put("ms","Malay");
        LANGUAGES.put("ml","Malayalam");
        LANGUAGES.put("mi","Maori");
        LANGUAGES.put("mr","Marathi");
        LANGUAGES.put("lus","Mizo");
        LANGUAGES.put("mn","Mongolian");
        LANGUAGES.put("mn-Mong","Mongolian (Traditional)");
        LANGUAGES.put("my","Myanmar");
        LANGUAGES.put("ne","Nepali");
        LANGUAGES.put("or","Odia");
        LANGUAGES.put("om","Oromo");
        LANGUAGES.put("pap","Papiamento");
        LANGUAGES.put("ps","Pashto");
        LANGUAGES.put("fa","Persian");
        LANGUAGES.put("pl","Polish");
        LANGUAGES.put("pt-BR","Portuguese (Brazilian)");
        LANGUAGES.put("pt-PT","Portuguese (European)");
        LANGUAGES.put("pa","Punjabi");
        LANGUAGES.put("otq","Querétaro Otomi");
        LANGUAGES.put("ro","Romanian");
        LANGUAGES.put("ru","Russian");
        LANGUAGES.put("sm","Samoan");
        LANGUAGES.put("sa","Sanskrit");
        LANGUAGES.put("gd","Scots Gaelic");
        LANGUAGES.put("nso","Sepedi");
        LANGUAGES.put("sr-Cyrl","Serbian (Cyrillic)");
        LANGUAGES.put("sr-Latn","Serbian (Latin)");
        LANGUAGES.put("st","Sesotho");
        LANGUAGES.put("sn","Shona");
        LANGUAGES.put("sd","Sindhi");
        LANGUAGES.put("si","Sinhala");
        LANGUAGES.put("sk","Slovak");
        LANGUAGES.put("sl","Slovenian");
        LANGUAGES.put("so","Somali");
        LANGUAGES.put("es","Spanish");
        LANGUAGES.put("su","Sundanese");
        LANGUAGES.put("sw","Swahili");
        LANGUAGES.put("sv","Swedish");
        LANGUAGES.put("ty","Tahitian");
        LANGUAGES.put("tg","Tajik");
        LANGUAGES.put("ta","Tamil");
        LANGUAGES.put("tt","Tatar");
        LANGUAGES.put("te","Telugu");
        LANGUAGES.put("th","Thai");
        LANGUAGES.put("bo","Tibetan");
        LANGUAGES.put("ti","Tigrinya");
        LANGUAGES.put("to","Tongan");
        LANGUAGES.put("ts","Tsonga");
        LANGUAGES.put("tr","Turkish");
        LANGUAGES.put("tk","Turkmen");
        LANGUAGES.put("tw","Twi");
        LANGUAGES.put("udm","Udmurt");
        LANGUAGES.put("uk","Ukrainian");
        LANGUAGES.put("hsb","Upper Sorbian");
        LANGUAGES.put("ur","Urdu");
        LANGUAGES.put("ug","Uyghur");
        LANGUAGES.put("uz","Uzbek");
        LANGUAGES.put("vi","Vietnamese");
        LANGUAGES.put("cy","Welsh");
        LANGUAGES.put("xh","Xhosa");
        LANGUAGES.put("sah","Yakut");
        LANGUAGES.put("yi","Yiddish");
        LANGUAGES.put("yo","Yoruba");
        LANGUAGES.put("yua","Yucatec Maya");
        LANGUAGES.put("zu","Zulu");
    }

    public static String getLanguageName(String code) {
        return LANGUAGES.getOrDefault(code, code);
    }

    public static String getLanguageCode(String name) {
        for (Map.Entry<String, String> entry : LANGUAGES.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(name)) {
                return entry.getKey();
            }
        }
        return name;
    }
    public static void main(String[] args) {
    }
}
