package com.saudi_sale.services;

import com.saudi_sale.models.DepartmentDataModel;
import com.saudi_sale.models.PlaceGeocodeData;
import com.saudi_sale.models.PlaceMapDetailsData;
import com.saudi_sale.models.ProductsDataModel;
import com.saudi_sale.models.RoomDataModel;
import com.saudi_sale.models.UserModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface Service {

    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);

    @FormUrlEncoded
    @POST("api/login")
    Call<UserModel> login(@Field("phone_code") String phone_code,
                          @Field("phone") String phone

    );

    @FormUrlEncoded
    @POST("api/register")
    Call<UserModel> signUpWithoutImage(@Field("name") String name,
                                       @Field("phone_code") String phone_code,
                                       @Field("phone") String phone,
                                       @Field("address") String address,
                                       @Field("latitude") double latitude,
                                       @Field("longitude") double longitude,
                                       @Field("software_type") String software_type
    );

    @Multipart
    @POST("api/register")
    Call<UserModel> signUpWithImage(@Part("name") RequestBody name,
                                    @Part("phone_code") RequestBody phone_code,
                                    @Part("phone") RequestBody phone,
                                    @Part("address") RequestBody address,
                                    @Part("latitude") RequestBody latitude,
                                    @Part("longitude") RequestBody longitude,
                                    @Part("software_type") RequestBody software_type,
                                    @Part MultipartBody.Part logo


    );

    @GET("api/home-link-filter")
    Call<ProductsDataModel> getProducts(@Query(value = "search_key") String search_key,
                                        @Query(value = "using_my_location") String using_my_location

    );

    @GET("api/offerSlider")
    Call<ProductsDataModel> getLatestOffer();

    @GET("api/allOffers")
    Call<ProductsDataModel> getOffer();

    @GET("api/all-categories")
    Call<DepartmentDataModel> getDepartment();

    @FormUrlEncoded
    @POST("api/my-chat-rooms")
    Call<RoomDataModel> getRooms(@Header("Authorization") String user_token,
                                 @Field("user_id") int user_id
    );

    @GET("api/productFilterByCategory")
    Call<ProductsDataModel> getFilteredProducts(@Query(value = "category_id") int category_id,
                                                @Query(value = "sub_category_id") int sub_category_id,
                                                @Query(value = "using_user_location") String using_my_location

    );

}