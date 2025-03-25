package iut.gon.agarioclient.model.map;

/**
 * Représente un noeud de l'arbre de la map
 * Ne pas confondre avec map.MapChunck
 * @see MapChunk
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

        NEnode.setDirection(Direction.NORTH_EAST);
        NWnode.setDirection(Direction.NORTH_WEST);
        SEnode.setDirection(Direction.SOUTH_EAST);
        SWnode.setDirection(Direction.SOUTH_WEST);
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
        NEnode.setDirection(Direction.NORTH_EAST);
    }

    public void setNWnode(MapElt NWnode) {
        this.NWnode = NWnode;
        NWnode.setDirection(Direction.NORTH_WEST);
    }

    public void setSEnode(MapElt SEnode) {
        this.SEnode = SEnode;
        SEnode.setDirection(Direction.SOUTH_EAST);
    }

    public void setSWnode(MapElt SWnode) {
        this.SWnode = SWnode;
        SWnode.setDirection(Direction.SOUTH_WEST);
    }
}
