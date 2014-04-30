package views.network.threeway;

import java.awt.Shape;
import org.apache.commons.collections15.Transformer;

/**
 * This transformer is used to determine what shape each vertex should be
 * when it is drawn on the screen. It actually just queries the object itself. 
 * @author rcurtis
 */
public class MyThreeWayVertexShapeTransformer implements Transformer<ThreeWayGraphNode, Shape>
{
    public Shape transform(ThreeWayGraphNode node)
    {
        return node.getNodeShape();
    }
}
