package edu.northeastern.numad22fa_team27.sticker_messenger;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import edu.northeastern.numad22fa_team27.sticker_messenger.interfaces.UserDaoConverter;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.IncomingMessage;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.OutgoingMessage;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.StickerTypes;
import edu.northeastern.numad22fa_team27.sticker_messenger.models.UserDAO;

public class FirebaseUserDaoConverter implements UserDaoConverter<DataSnapshot, UserDAO> {
    private static List<IncomingMessage> marshalIncoming(DataSnapshot snapshot) {
        return StreamSupport.stream(snapshot.getChildren().spliterator(), false)
                .map(e -> new IncomingMessage(
                        (Date)e.child("dateSent").getValue(Date.class),
                        (String)e.child("sourceUser").getValue(String.class),
                        (StickerTypes)e.child("sticker").getValue(StickerTypes.class)
                ))
                .collect(Collectors.toList());
    }

    private static List<OutgoingMessage> marshalOutgoing(DataSnapshot snapshot) {
        return StreamSupport.stream(snapshot.getChildren().spliterator(), false)
                .map(e -> new OutgoingMessage(
                        (Date)e.child("dateSent").getValue(Date.class),
                        (String)e.child("destUser").getValue(String.class),
                        (StickerTypes)e.child("sticker").getValue(StickerTypes.class)
                ))
                .collect(Collectors.toList());
    }

    public UserDAO convert(DataSnapshot snapshot){
        List<String> friends = new ArrayList<>();
        List<IncomingMessage> incomingMessages = new ArrayList<>();
        List<OutgoingMessage> outgoingMessages = new ArrayList<>();
        for(DataSnapshot ds : snapshot.getChildren()) {
            String key = Objects.requireNonNull(ds.getKey());
            try {
                switch (key) {
                    case "friends": {
                        friends = (List<String>) ds.getValue();
                        break;
                    }
                    case "incomingMessages": {
                        incomingMessages = marshalIncoming(ds);
                        break;
                    }
                    case "outgoingMessages": {
                        outgoingMessages = marshalOutgoing(ds);
                        break;
                    }
                    default:
                        break;
                }
            } catch (Exception e) {
                // Creation of lists failed - this would happen if data present cannot be marshalled
                // into expected datatype.
                return null;
            }
        }

        return new UserDAO(
                snapshot.getKey(),
                friends,
                incomingMessages,
                outgoingMessages
        );
    }
}
