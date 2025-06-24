package tests.message;

import io.restassured.response.ValidatableResponse;
import models.common.Message;
import models.response.MessageResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static constants.ApiConstants.MESSAGE_ENDPOINT;
import static tests.base.BaseTest.givenRequest;
import static tests.utils.assertions.CommonAssertions.assertJsonFieldType;
import static tests.utils.assertions.CommonAssertions.assertNotNullOrBlank;


public class GetMessagesTests {

    private static MessageResponse messages;
    private static ValidatableResponse response;

    @BeforeAll
    public static void fetchMessage() {
        response = givenRequest()
                .when()
                .get(MESSAGE_ENDPOINT)
                .then()
                .statusCode(200);

        messages = response.extract().as(MessageResponse.class);
        assertNotNullOrBlank(messages, "Message Response Object");
    }

    @Test
    @DisplayName("Should return a non-empty list of messages")
    public void testMessagesListIsNotEmpty() {
        assertNotNullOrBlank(messages.getMessages(), "Message Response Object");
    }

    /**
     * Provides a stream of arguments, covering required fields of all messages in the list.
     * Each argument contains: (fieldValue, displayLabel).
     */
    static Stream<Arguments> requiredFieldsProvider() {
        List<Message> messageList = messages.getMessages();

        return IntStream.range(0, messageList.size())
                .boxed()
                .flatMap(index -> {
                    Message msg = messageList.get(index);
                    return Stream.of(
                            Arguments.of(msg.getId(), String.format("Message %d - Id", index)),
                            Arguments.of(msg.getName(), String.format("Message %d - Name", index)),
                            Arguments.of(msg.getSubject(), String.format("Message %d - Subject", index)),
                            Arguments.of(msg.getRead(), String.format("Message %d - Read", index))
                    );
                });
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("requiredFieldsProvider")
    @DisplayName("Should return messages with all required fields present")
    public void testBrandingStructure(Object value, String displayName) {
        assertNotNullOrBlank(value, displayName);
    }

    /**
     * Provides a stream of arguments for parameterized tests, verifying the JSON field types of
     * each message returned in the response.
     */
    static Stream<Arguments> fieldTypeProvider() {
        List<Message> messageList = messages.getMessages();

        return IntStream.range(0, messageList.size())
                .boxed()
                .flatMap(index -> Stream.of(
                        Arguments.of(String.format("messages[%d].id", index),
                                String.format("Message %d - Id", index), Integer.class, "Integer"),
                        Arguments.of(String.format("messages[%d].name", index),
                                String.format("Message %d - Name", index), String.class, "String"),
                        Arguments.of(String.format("messages[%d].subject", index),
                                String.format("Message %d - Subject", index), String.class, "String"),
                        Arguments.of(String.format("messages[%d].read", index),
                                String.format("Message %d - Read", index), Boolean.class, "Boolean")
                ));
    }

    @ParameterizedTest(name = "{1} - Type {3}")
    @MethodSource("fieldTypeProvider")
    @DisplayName("Should return messages with correct JSON structure and types")
    public void testMessagesJsonStructureAndTypes(String jsonPath, String displayName,
                                                  Class<?> expectedType, String typeString) {
        assertJsonFieldType(response, jsonPath, expectedType);
    }
}
