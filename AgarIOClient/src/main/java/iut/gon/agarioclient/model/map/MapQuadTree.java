package iut.gon.agarioclient.model.map;

/**
 * Représente un noeud de l'arbre de la map
 * Ne pas confondre avec map.MapChunck
 * @see MapChunck
 */
public class MapQuadTree extends MapElt {
    private MapElt NEnode;
    private MapElt NWnode;
    private MapElt SEnode;
    private MapElt SWnode;


    public MapQuadTree(MapElt NEnode, MapElt NWnode, MapElt SEnode, MapElt SWnode, MapQuadTree parent) {
        super(parent, false);
        this.NEnode = NEnode;
        this.NWnode = NWnode;
        this.SEnode = SEnode;
        this.SWnode = SWnode;
    }

    public MapQuadTree(MapQuadTree parent) {
        super(parent, false);
    }

    public MapQuadTree(){
        super(null, false);
    }

    public MapElt getNEnode() {
        return NEnode;
    }

    public MapElt getNWnode() {
        return NWnode;
    }

    public MapElt getSEnode() {
        return SEnode;
    }

    public MapElt getSWnode() {
        return SWnode;
    }

    public void setNEnode(MapElt NEnode) {
        this.NEnode = NEnode;
    }

    public void setNWnode(MapElt NWnode) {
        this.NWnode = NWnode;
    }

    public void setSEnode(MapElt SEnode) {
        this.SEnode = SEnode;
    }

    public void setSWnode(MapElt SWnode) {
        this.SWnode = SWnode;
    }
}
