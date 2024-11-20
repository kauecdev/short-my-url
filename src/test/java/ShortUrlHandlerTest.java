import com.amazonaws.services.lambda.runtime.Context;
import com.kauecdev.shortmyurl.ShortUrlHandler;

import org.junit.Test;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ShortUrlHandlerTest {


    @Test
    public void testShortUrlSuccessfully() {
        S3Client mockS3Client = mock(S3Client.class);
        ShortUrlHandler handler = new ShortUrlHandler(mockS3Client);

        Map<String, Object> input = new HashMap<>();
        input.put("body", "{\"originalUrl\":\"http://example.com\",\"expirationTime\":\"3600\"}");
        Context context = mock(Context.class);

        Map<String, String> response = handler.handleRequest(input, context);

        assertNotNull(response.get("code"));
        assertEquals(8, response.get("code").length());
    }

    @Test
    public void testInvalidJsonInput_ThrowsRuntimeException() {
        ShortUrlHandler handler = new ShortUrlHandler();

        Map<String, Object> input = new HashMap<>();
        input.put("body", "invalid json");
        Context context = mock(Context.class);

        assertThrows(RuntimeException.class, () -> {
            handler.handleRequest(input, context);
        });
    }
}