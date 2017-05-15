package pt.ulisboa.tecnico.cmov.locmess.activities.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import pt.ulisboa.tecnico.cmov.locmess.R;
import pt.ulisboa.tecnico.cmov.locmess.activities.model.types.Location;
import pt.ulisboa.tecnico.cmov.locmess.activities.model.types.Message;
import pt.ulisboa.tecnico.cmov.locmess.activities.model.types.ProfileKeypair;

/**
 * Created by joaod on 03-Apr-17.
 */

public class TestData {

    private static final String[] contentExamples = {"Nothingness cannot be defined",
            "Time is like a river made up of the events which happen, and a violent stream; " +
                    "for as soon as a thing has been seen, it is carried away, and another comes" +
                    " in its place, and this will be carried away too,",
            "But when I know that the glass is already broken, every minute with it is precious.",
            "For me, it is far better to grasp the Universe as it really is than to persist in" +
                    " delusion, however satisfying and reassuring.",
            "The seeker after the truth is not one who studies the writings of the ancients and," +
                    " following his natural disposition, puts his trust in them, but rather the" +
                    " one who suspects his faith in them and questions what he gathers from them," +
                    " the one who submits to argument and demonstration, and not to the " +
                    "sayings of a human being whose nature is fraught with all kinds " +
                    "of imperfection and deficiency.",
            "You must take personal responsibility. You cannot change the circumstances, the" +
                    " seasons, or the wind, but you can change yourself. That is something you" +
                    " have charge of."
    };
    private static final String[] owners = {"Bruce Lee",
            "Marcus Aurelius",
            "Meng Tzu",
            "Ajahn Chah",
            "Carl Sagan",
            "Alhazen",
            "Jim Rohn"

    };

    private static final String[] locations = {
            "IST TagusPark",
            "IST Alameda",
            "Arco do Cego Park"
    };

    private static final String[] coordinates = {
            "[38.737566, -9.303119, 20m]",
            "[38.736750, -9.139370, 20m]",
            "[38,734383, -9.140388, 20m]",
    };

    private static final Double[] latitudes = {
            38.737566,
            38.736750,
            38.734383

    };

    private static final Double[] longitudes = {
            -9.303119,
            -9.139370,
            -9.140388

    };

    private static final int[] radius = {
            20,
            20,
            20
    };
    private static final String[] keys = {
            "club",
            "course",
            "school"
    };

    private static final String[] values = {
            "Real Madrid",
            "MEIC",
            "IST"
    };

    private static final int icon = R.drawable.ic_wifi_black_36dp;

    public static List<Message> getDummyMessages() {
        List<Message> data = new ArrayList<>();

        for (int x = 0; x < 4; x++) {
            for (int i = 0; i < contentExamples.length && i < owners.length; i++) {
                Message item = new Message("Title " + (i + x * contentExamples.length), contentExamples[i], owners[i], null, null, false, null);
                data.add(item);
            }

        }

        return data;
    }

    public static List<Location> getDummyLocations() {
        List<Location> data = new ArrayList<>();

        for (int i = 0; i < locations.length; i++)
            data.add(new Location(locations[i], latitudes[i], longitudes[i], radius[i] ));
        //add ssid location
        List<String> ssidList = new ArrayList<>();
        ssidList.add("eduroam");
        data.add(new Location("TagusPark (eduroam)", ssidList));

        return data;

    }

    public static List<ProfileKeypair> getProfileKeyPairs() {
        List<ProfileKeypair> data = new ArrayList<>();

        for (int i = 0; i < keys.length; i++)
            data.add(new ProfileKeypair(keys[i], values[i]));

        return data;
    }

    public static List<String> getLocations() {
        List<String> locations = new ArrayList<>(Arrays.asList(TestData.locations));

        for (int i = 0; i < 40; i++) {
            locations.add("location " + i);
        }
        return locations;
    }


    public static Message getRandomMessage() {
        int rand = new Random().nextInt(6);
        return new Message("Title " + (String.valueOf(rand)), contentExamples[rand], owners[rand], null, null, false, null);
    }

    public static List<String> getExistingKeys() {
        List<String> existingKeys = new ArrayList<>();
        for (String key : keys)
            existingKeys.add(key);

        return existingKeys;
    }
}
