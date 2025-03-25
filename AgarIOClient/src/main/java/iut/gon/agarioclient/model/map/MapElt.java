package iut.gon.agarioclient.model.map;

/**
 * element de l'abre de la map.
 * Possède un parent, et une proprriété isChunck
 */
public class MapElt {
    private MapQuadTree parent;

    private final boolean isChunck;

    /**
     *
     * @param parent map.MapElt representant le parent de l'element
     */
    public MapElt(MapQuadTree parent, boolean isChunck){
        this.parent = parent;
        this.isChunck = isChunck;
    }

    /**
     *
     * @return map.MapElt le parent de l'élement
     */
    public MapQuadTree getParent() {
        return parent;
    }

    public void setParent(MapQuadTree parent) {
        this.parent = parent;
    }

    public boolean isChunck(){
        return this.isChunck;
    }
}
