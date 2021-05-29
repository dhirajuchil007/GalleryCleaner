package com.velocityappsdj.gallerycleaner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface ApplicationConstants {
    HashMap<String, String> sort_order = new HashMap() {{
        put("most recent first", "date");
        put("Bigger file first", "size");
        put("Alphabetically", "name");
    }};

    HashMap<String, String> sort_order_pref_to_text = new HashMap() {{
        put("date", "most recent first");
        put("size", "Bigger file first");
        put("name", "Alphabetically");
    }};
    String ALL = "All";
}
