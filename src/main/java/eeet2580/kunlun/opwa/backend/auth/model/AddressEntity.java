package eeet2580.kunlun.opwa.backend.auth.model;

import lombok.Data;

@Data
public class AddressEntity {
    private String number;
    private String street;
    private String ward;
    private String district;
    private String city;
}
