package fr.icefeather.wsn.client.application.util.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class HeadersMapAdapter extends XmlAdapter<HeadersMapElements[], Map<String, String>> {
    public HeadersMapElements[] marshal(Map<String, String> arg0) throws Exception {
        HeadersMapElements[] mapElements = new HeadersMapElements[arg0.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : arg0.entrySet())
            mapElements[i++] = new HeadersMapElements(entry.getKey(), entry.getValue());

        return mapElements;
    }

    public Map<String, String> unmarshal(HeadersMapElements[] arg0) throws Exception {
        Map<String, String> r = new HashMap<String, String>();
        for (HeadersMapElements mapelement : arg0)
            r.put(mapelement.key, mapelement.value);
        return r;
    }
}