package ua.markiyan.sonara_parser.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LastFmTrackResponse {
    private TrackData track;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TrackData {
        private String listeners;
        private String playcount;
        private Wiki wiki;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Wiki {
        private String summary;
        private String content;
    }
}
