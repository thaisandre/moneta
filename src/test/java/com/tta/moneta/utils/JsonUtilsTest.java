package com.tta.moneta.utils;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.tta.moneta.utils.JsonUtils.toJson;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.skyscreamer.jsonassert.JSONCompareMode.LENIENT;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

class JsonUtilsTest {

    @Test
    void toJson__should_return_null_as_string_when_object_is_null() {
        assertThat(toJson(null)).isEqualTo("null");
    }

    @Test
    void toJson__should_convert_empty_objects_to_json() throws JSONException {
        assertEquals(toJson(new EmptyObject()), "{}", NON_EXTENSIBLE);
        assertEquals(toJson(emptyList()), "[]", NON_EXTENSIBLE);
    }

    @Test
    void toJson__should_convert_object_to_json() throws JSONException {
        LocalDate creationDate = LocalDate.of(2023, 07, 18);
        BillTeste bill1 = new BillTeste(1L,"Conta de luz", creationDate);
        BillTeste bill2 = new BillTeste(2L, "Conta de água", creationDate.plusDays(1));

        UserTest userTest1 = new UserTest("User1", LocalDate.of(1980, 07, 20),
                List.of(bill1, bill2));

        assertEquals(toJson(userTest1), """
        {
            "name": "User1",
            "birthDate": "20/07/1980",
            "bills": [
                {
                    "id": 1,
                    "description": "Conta de luz",
                    "expirationDate": "2023-07-18"
                },
                {
                    "id": 2,
                    "description": "Conta de água",
                    "expirationDate": "2023-07-19"
                }
            ]
        }""", NON_EXTENSIBLE);
    }

    @Test
    void toJson__should_convert_a_collection_of_objects_to_json() throws JSONException {
        LocalDate creationDate = LocalDate.of(2023, 07, 18);
        BillTeste bill1 = new BillTeste(1L,"Conta de luz", creationDate);
        BillTeste bill2 = new BillTeste(2L, "Conta de água", creationDate.plusDays(1));

        LocalDate birthDate = LocalDate.of(1980, 07, 20);
        UserTest userTest1 = new UserTest("User1", birthDate, List.of(bill1, bill2));
        UserTest userTest2 = new UserTest("User2", birthDate.plusYears(1), List.of(bill1));

        assertEquals(toJson(List.of(userTest1, userTest2)), """
        [
            {
                "name": "User1",
                "birthDate": "20/07/1980",
                "bills": [
                    {
                        "id": 1,
                        "description": "Conta de luz",
                        "expirationDate": "2023-07-18"
                    },
                    {
                        "id": 2,
                        "description": "Conta de água",
                        "expirationDate": "2023-07-19"
                    }
                ]
            },
            {
                "name": "User2",
                "birthDate": "20/07/1981",
                "bills": [
                    {
                        "id": 1,
                        "description": "Conta de luz",
                        "expirationDate": "2023-07-18"
                    }
                ]
            }
        ]""", NON_EXTENSIBLE);
    }

record UserTest(String name,
                @JsonFormat(shape = STRING, pattern = "dd/MM/yyyy") LocalDate birthDate,
                List<BillTeste> bills){}

record BillTeste(Long id,
                 String description,
                 @JsonFormat(shape = STRING, pattern = "yyy-MM-dd") LocalDate expirationDate){}

record EmptyObject(){}

}