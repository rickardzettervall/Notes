package tech.zettervall.notes.utils;

import java.util.ArrayList;
import java.util.List;

import tech.zettervall.notes.models.Note;
import tech.zettervall.notes.repositories.NoteRepository;

public abstract class DummyDataUtil {

    private static String note1Title = "New Games";
    private static String note1Text = "Lorem ipsum dolor sit amet consectetur adipiscing," +
            " elit faucibus himenaeos scelerisque nostra, molestie ac pulvinar diam quisque." +
            " Feugiat quisque massa est euismod etiam hac aptent et eget urna ridiculus, arcu" +
            " dictumst iaculis parturient vehicula cras integer odio potenti id, commodo vel" +
            " eu primis lectus semper eleifend orci ante tristique. Dictumst nulla taciti" +
            " torquent varius imperdiet proin parturient laoreet, nunc nisl dictum dapibus" +
            " ut mus tortor, in libero montes mollis faucibus penatibus curae.";
    private static String note2Title = "Shopping List";
    private static String note2Text = "Pharetra congue maecenas dapibus urna tempor cum aliquet nisi," +
            " imperdiet habitasse ad tempus felis orci velit molestie torquent, consequat hendrerit" +
            " odio elementum pretium sodales luctus.";
    private static String note3Title = "";
    private static String note3Text = "This is a note without any title, but that's ok." +
            " A title is of course encouraged but not required.";
    private static String note4Title = "Car stuff";
    private static String note4Text = "Feugiat quisque massa est euismod etiam hac aptent et eget " +
            "urna ridiculus, arcu dictumst iaculis parturient vehicula cras integer odio potenti id";
    private static String note5Title = "Favorite Movies";
    private static String note5Text = "Enim pellentesque tincidunt ridiculus sapien ac sem porttitor" +
            " nec mauris eu, aptent etiam vivamus himenaeos sodales curae dignissim commodo at," +
            " purus laoreet condimentum morbi ultrices aliquam eleifend consequat auctor.";
    private static String note6Title = "University courses this year";
    private static String note6Text = "Sollicitudin taciti sed aenean dictum ullamcorper quisque" +
            " molestie tincidunt metus, eleifend integer curabitur orci eu vestibulum feugiat" +
            " nulla netus donec, praesent neque ridiculus dis ac tristique arcu libero.";

    public static void insertDummyData(NoteRepository noteRepository) {
        Note[] notes = {
                new Note(note1Title,
                        note1Text,
                        null,
                        getTagIDs(1),
                        DateTimeUtil.getCurrentEpoch(),
                        DateTimeUtil.getCurrentEpoch(),
                        -1,
                        false,
                        true),

                new Note(note2Title,
                        note2Text,
                        null,
                        getTagIDs(1),
                        DateTimeUtil.getCurrentEpoch() + 1,
                        DateTimeUtil.getCurrentEpoch() + 1,
                        -1,
                        false,
                        true),

                new Note(note3Title,
                        note3Text,
                        null,
                        getTagIDs(),
                        DateTimeUtil.getCurrentEpoch() + 2,
                        DateTimeUtil.getCurrentEpoch() + 2,
                        -1,
                        false,
                        false),

                new Note(note4Title,
                        note4Text,
                        null,
                        getTagIDs(1),
                        DateTimeUtil.getCurrentEpoch() + 3,
                        DateTimeUtil.getCurrentEpoch() + 3,
                        -1,
                        false,
                        false),

                new Note(note5Title,
                        note5Text,
                        null,
                        getTagIDs(),
                        DateTimeUtil.getCurrentEpoch() + 4,
                        DateTimeUtil.getCurrentEpoch() + 4,
                        -1,
                        false,
                        true),

                new Note(note6Title,
                        note6Text,
                        null,
                        getTagIDs(2),
                        DateTimeUtil.getCurrentEpoch() + 5,
                        DateTimeUtil.getCurrentEpoch() + 5,
                        -1,
                        false,
                        false)
        };

        noteRepository.insertNotes(notes);
    }

    private static List<Integer> getTagIDs(int... tagIDs) {
        List<Integer> tagIDsList = new ArrayList<>();
        for (int i : tagIDs) {
            tagIDsList.add(i);
        }
        return tagIDsList;
    }
}
