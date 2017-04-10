package pt.ulisboa.tecnico.cmov.locmess.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pt.ulisboa.tecnico.cmov.locmess.R;

/**
 * Created by joaod on 03-Apr-17.
 */

public class TestData {

    private static final String[] titles = {"Nothingness cannot be defined",
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
    private static final String[] subTitles = {"Bruce Lee",
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

    private static final int icon = R.drawable.ic_wifi_black_36dp;

    public static List<Message> getListData() {
        List<Message> data = new ArrayList<>();

        for (int x = 0; x < 4; x++) {
            for (int i = 0; i < titles.length && i < subTitles.length; i++) {
                Message item = new Message(titles[i], null, subTitles[i],false);
                item.setTitle(titles[i]);
                item.setSubTitle(subTitles[i]);
                data.add(item);
            }

        }

        return data;
    }

    public static List<Message> getListData(String[] titles, String[] subTitles) {
        List<Message> data = new ArrayList<>();
        for (int x = 0; x < 4; x++) {
            for (int i = 0; i < titles.length && i < subTitles.length; i++) {
                Message item = new Message(titles[i], null, subTitles[i],false);
                item.setTitle(titles[i]);
                item.setSubTitle(subTitles[i]);
                data.add(item);
            }
        }
        return data;
    }

    public static List<Message> getLocationsData(){
        return getListData(locations, coordinates);
    }

    public static Message getRandomMessage(){
        int rand = new Random().nextInt(6);

        Message item = new Message(titles[rand], null, subTitles[rand],false);


        item.setTitle(titles[rand]);
        item.setSubTitle(subTitles[rand]);

        return item;
    }
}
