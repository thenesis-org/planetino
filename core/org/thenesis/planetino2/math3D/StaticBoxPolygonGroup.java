package org.thenesis.planetino2.math3D;

import org.thenesis.planetino2.util.Vector;

public class StaticBoxPolygonGroup extends BoxPolygonGroup {
	
	protected Vector transformedPolygonCache;

	public StaticBoxPolygonGroup(BoxModel boxDef, Vector3D location, float scale) {
		super(boxDef, location, scale);
		cacheTransformedPolygons();
	}
	
	private void cacheTransformedPolygons() {
		transformedPolygonCache = new Vector(6);
		resetIterator();
		while(hasNext()) {
			TexturedPolygon3D polygon = new TexturedPolygon3D();
			super.nextPolygonTransformed(polygon);
			transformedPolygonCache.add(polygon);
		}
	}
	
	@Override
	public void nextPolygonTransformed(Polygon3D cache) {
//		if (transformedPolygonCache == null) {
//			cacheTransformedPolygons();
//		}
		//System.out.println(iteratorIndex);
		Polygon3D polygon = (Polygon3D)transformedPolygonCache.elementAt(iteratorIndex);
		cache.setTo(polygon);
		iteratorIndex++;
	}

}
