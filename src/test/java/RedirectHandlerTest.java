import com.amazonaws.services.lambda.runtime.Context;
import com.kauecdev.shortmyurl.RedirectHandler;

import org.junit.Test;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RedirectHandlerTest {

    @Test
    public void testRedirectSuccessfully() {
        Map<String, Object> input = new HashMap<>();
        input.put("rawPath", "/randomCode");

        S3Client mockS3Client = mock(S3Client.class);

        RedirectHandler redirectHandler = new RedirectHandler(mockS3Client);

        Context context = mock(Context.class);

        long tomorrowDateInSeconds = (System.currentTimeMillis() / 1000) + (60 * 60 * 24);

        byte[] mockData = String.format("{\"originalUrl\":\"example\",\"expirationTime\":%d}", tomorrowDateInSeconds).getBytes();
        AbortableInputStream byteArrayInputStream = AbortableInputStream.create(new ByteArrayInputStream(mockData));
        GetObjectResponse mockResponseMetadata = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> mockResponseInputStream = spy(new ResponseInputStream<>(mockResponseMetadata, byteArrayInputStream));

        when(mockS3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponseInputStream);

        Map<String, Object> response = redirectHandler.handleRequest(input, context);

        assertEquals(302, response.get("statusCode"));
        assertTrue(((Map<String, String>) response.get("headers")).containsKey("Location"));
    }

    @Test
    public void testAccessToExpiredLink() {
        Map<String, Object> input = new HashMap<>();
        input.put("rawPath", "/randomCode");

        S3Client mockS3Client = mock(S3Client.class);

        RedirectHandler redirectHandler = new RedirectHandler(mockS3Client);

        Context context = mock(Context.class);

        long yesterdayDateInSeconds = (System.currentTimeMillis() / 1000) - (60 * 60 * 24);

        byte[] mockData = String.format("{\"originalUrl\":\"example\",\"expirationTime\":%d}", yesterdayDateInSeconds).getBytes();
        AbortableInputStream byteArrayInputStream = AbortableInputStream.create(new ByteArrayInputStream(mockData));
        GetObjectResponse mockResponseMetadata = GetObjectResponse.builder().build();
        ResponseInputStream<GetObjectResponse> mockResponseInputStream = spy(new ResponseInputStream<>(mockResponseMetadata, byteArrayInputStream));

        when(mockS3Client.getObject(any(GetObjectRequest.class))).thenReturn(mockResponseInputStream);

        Map<String, Object> response = redirectHandler.handleRequest(input, context);

        assertEquals(410, response.get("statusCode"));
        assertNull(response.get("headers"));
    }

    @Test
    public void testEmptyShortUrlCode_ThrowsIllegalArgumentException() {
        RedirectHandler redirectHandler = new RedirectHandler();

        Map<String, Object> input = new HashMap<>();
        input.put("rawPath", "/");
        Context context = mock(Context.class);

        assertThrows(IllegalArgumentException.class, () -> {
            redirectHandler.handleRequest(input, context);
        });
    }
}