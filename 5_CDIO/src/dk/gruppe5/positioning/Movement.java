package dk.gruppe5.positioning;

import dk.gruppe5.model.Circle;
import dk.gruppe5.model.DPoint;

public class Movement {

	Runnable thread = new Runnable(){
		@Override 
		public void run() {

		}
	};
	
	
	
	/**
	 * Afgør om en linje, der går gennem <param>lStart</param> og <param>lEnd</param>, 
	 * skærer en cirkel 
	 * @param lStart startpunkt på linjen
	 * @param lEnd slutpunkt på linjen
	 * @param circle den cirkel der skal tjekkes for
	 * @return
	 */
	
	public boolean doesLineAndCircleIntersect(DPoint lStart, DPoint lEnd, Circle circle) {
		DPoint closestOnLine = closestPointOnLine(lStart, lEnd, circle.c);
		if(closestOnLine.distance(circle.c) < circle.r) {
			return true;
		}
		return false;
	}
	
	/**
	 * Giver det punkt på linjen, der går gennem <param>lStart</param> og <param>lEnd</param>, 
	 * som ligger tættest på cirklens centrum.
	 * @param lStart startpunkt på vektoren på linjen
	 * @param lEnd slutpunkt på vektoren på linjen
	 * @param circleC centrum på cirklen
	 * @return det punkt på linjen, der er tættest på cirklens centrum
	 */
	
	public DPoint closestPointOnLine(DPoint lStart, 
			DPoint lEnd, DPoint circleC){
		double 	ly2 = lEnd.y,
				ly1 = lStart.y,
				lx2 = lEnd.x,
				lx1 = lStart.x,
				x0 = circleC.x,
				y0 = circleC.y;
		double A1 = ly2 - ly1; 
		double B1 = lx1 - lx2; 
		double C1 = (ly2 - ly1)*lx1 + (lx1 - lx2)*ly1; 
		double C2 = -B1*x0 + A1*y0; 
		double det = A1*A1 - -B1*B1; 
		double cx = 0; 
		double cy = 0; 
		if(det != 0){ 
			cx = (float)((A1*C1 - B1*C2)/det); 
			cy = (float)((A1*C2 - -B1*C1)/det); 
		}else{ 
			cx = x0; 
			cy = y0; 
		} 
		return new DPoint(cx, cy); 
	}



}
