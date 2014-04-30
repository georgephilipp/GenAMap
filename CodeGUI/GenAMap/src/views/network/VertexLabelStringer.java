package views.network;

import datamodel.Trait;
import org.apache.commons.collections15.Transformer;

class VertexLabelStringer implements Transformer<Trait,String> {

    boolean enabled=true;

    public VertexLabelStringer(boolean e)
    {
        enabled=e;
    }

    public String transform(Trait e)
    {
        if(enabled)
            return e.getName();
        return "";
    }
}