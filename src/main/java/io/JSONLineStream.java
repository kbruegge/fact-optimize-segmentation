package io;

import com.google.common.primitives.Doubles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import stream.Data;
import stream.data.DataFactory;
import stream.io.AbstractStream;
import stream.io.SourceURL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Read a .jsonl source for streaming.
 * Created by kai on 09.02.16.
 */
public class JSONLineStream extends AbstractStream {
    Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().enableComplexMapKeySerialization().create();
    private BufferedReader br;

    public JSONLineStream(SourceURL url) {
        super(url);
    }

    @Override
    public void init() throws Exception {
        super.init();
        br = new BufferedReader(new InputStreamReader(url.openStream()));
    }

    @Override
    public Data readNext() throws Exception {
        String line = br.readLine();
        Type listType = new TypeToken<HashMap<String, Object>>() {}.getType();
        HashMap<String, Serializable> item = gson.fromJson(line, listType);
        if (item  == null) {
            return null;
        }

        ArrayList<? extends Number> l = new ArrayList<>();
        for(Map.Entry<String, Serializable> e : item.entrySet()){
            if(e.getValue().getClass().isAssignableFrom(l.getClass())){
                @SuppressWarnings("unchecked")
                ArrayList<Double> list = (ArrayList<Double>) e.getValue();
                e.setValue(Doubles.toArray(list));
            }
        }

        return DataFactory.create(item);
    }

}
