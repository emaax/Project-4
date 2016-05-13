package com.example.em.spotifymusicplayer;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Image;

/**
 * Created by emiliaaxen on 16-05-11.
 * This class is responsible for
 *
 */
public class ArtistListData implements Parcelable {
    String artistName;
    String artistId;
    String artistImage;

    public ArtistListData(Artist artist) {
        artistName = artist.name;
        artistId = artist.id;
        for(Image image : artist.images) {
            if (image.width >= 150 && image.width <= 300) {
                artistImage = image.url;
                break;
            }
        }
        }


    private ArtistListData(Parcel parcel) {
        artistName = parcel.readString();
        artistId = parcel.readString();
        artistImage = parcel.readString();
    }

   /* private void ReadFromParcel(Parcel in) {
        artistName = in.readString();
        artistId = in.readString();
        artistImage = in.readString();
    }*/
   @Override
   public int describeContents() {
       return 0;
   }
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(artistName);
        parcel.writeString(artistId);
        parcel.writeString(artistImage);
    }



    public static final Parcelable.Creator<ArtistListData> CREATOR = new Parcelable.Creator<ArtistListData>() {
        public ArtistListData createFromParcel(Parcel in) {
            return new ArtistListData(in);
        }

        public ArtistListData[] newArray(int size) {
            return new ArtistListData[size];
        }
    };
}
