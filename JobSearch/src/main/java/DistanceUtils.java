/**
 * DistanceUtils provides a utility method to calculate the distance
 * between two geographical points using the Haversine formula.
 */
public class DistanceUtils {

    // Private constructor to prevent instantiation
    private DistanceUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Calculates the distance between two coordinates (latitude and longitude) in kilometers.
     *
     * @param lat1 Latitude of point 1 (in degrees)
     * @param lon1 Longitude of point 1 (in degrees)
     * @param lat2 Latitude of point 2 (in degrees)
     * @param lon2 Longitude of point 2 (in degrees)
     * @return Distance between the two points in kilometers
     */
    public static double calculate(double lat1, double lon1, double lat2, double lon2) {
        int R = 6371; // Radius of the Earth in kilometers

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
