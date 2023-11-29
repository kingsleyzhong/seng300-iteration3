package com.thelocalmarketplace.software.membership;
/*
 *
 * Project Iteration 3 Group 1
 *
 * Derek Atabayev 			: 30177060
 * Enioluwafe Balogun 		: 30174298
 * Subeg Chahal 			: 30196531
 * Jun Heo 					: 30173430
 * Emily Kiddle 			: 30122331
 * Anthony Kostal-Vazquez 	: 30048301
 * Jessica Li 				: 30180801
 * Sua Lim 					: 30177039
 * Savitur Maharaj 			: 30152888
 * Nick McCamis 			: 30192610
 * Ethan McCorquodale 		: 30125353
 * Katelan Ng 				: 30144672
 * Arcleah Pascual 			: 30056034
 * Dvij Raval 				: 30024340
 * Chloe Robitaille 		: 30022887
 * Danissa Sandykbayeva 	: 30200531
 * Emily Stein 				: 30149842
 * Thi My Tuyen Tran 		: 30193980
 * Aoi Ueki 				: 30179305
 * Ethan Woo 				: 30172855
 * Kingsley Zhong 			: 30197260
 */
public class Member {
    private int points;
    private final String name;
    /** An instance of a Member for any membership program the client may be interested in.
     * @param name The member's name. */
    protected Member(String name) {
        points = 0;
        this.name = name;
    }

    /** Returns the member's name.
     * @return The member's name. */
    public String getName() {
        return name;
    }

    /** Returns the member's number of points.
     * @return The member's number of points.*/
    public int getPoints() {
        return points;
    }

    /** Changes the member's number of points.
     * @param delta The amount to change the points by. Can be positive to add, or negative to subtract. */
    public void changePoints(int delta) {
        points += delta;
    }
}
