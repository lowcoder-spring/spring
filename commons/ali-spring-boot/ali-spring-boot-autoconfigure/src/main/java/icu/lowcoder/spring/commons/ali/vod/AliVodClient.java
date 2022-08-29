package icu.lowcoder.spring.commons.ali.vod;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.*;
import icu.lowcoder.spring.commons.ali.vod.model.UploadVideoVoucher;
import icu.lowcoder.spring.commons.util.json.JsonUtils;
import icu.lowcoder.spring.commons.util.spring.BeanUtils;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AliVodClient {
    private final DefaultAcsClient acs;
    private final AliVodProperties vodProperties;

    public AliVodClient(DefaultAcsClient acs, AliVodProperties vodProperties) {
        this.acs = acs;
        this.vodProperties = vodProperties;
    }

    @SneakyThrows
    public UploadVideoVoucher uploadVideoVoucher(String title, String fileName, String userId) {
        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setTitle(title);
        request.setFileName(fileName);

        //UserData，用户自定义设置参数，用户需要单独回调URL及数据透传时设置(非必须)
        //JSONObject userData = new JSONObject();

        //UserData回调部分设置
        //JSONObject messageCallback = new JSONObject();
        //messageCallback.put("CallbackURL", "http://192.168.0.0/16");
        //messageCallback.put("CallbackType", "http");
        //userData.put("MessageCallback", messageCallback.toJSONString());

        //UserData透传数据部分设置
        //JSONObject extend = new JSONObject();
        //extend.put("MyId", "user-defined-id");
        //userData.put("Extend", extend.toJSONString());

        //request.setUserData(userData.toJSONString());
        return BeanUtils.instantiate(UploadVideoVoucher.class, acs.getAcsResponse(request));
    }

    @SneakyThrows
    public UploadVideoVoucher refreshUploadVideo(String videoId) {
        RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
        request.setVideoId(videoId);
        return BeanUtils.instantiate(UploadVideoVoucher.class, acs.getAcsResponse(request));
    }

    @SneakyThrows
    public GetVideoInfosResponse getVideoInfos(List<String> videoIds) {
        GetVideoInfosRequest request = new GetVideoInfosRequest();
        request.setVideoIds(StringUtils.collectionToCommaDelimitedString(videoIds));
        return acs.getAcsResponse(request);
    }

    @SneakyThrows
    public GetVideoPlayAuthResponse playAuth(String videoId) {
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoId);
        return acs.getAcsResponse(request);
    }

    @SneakyThrows
    public GetCategoriesResponse getCategories(Long categoryId) {
        GetCategoriesRequest request = new GetCategoriesRequest();
        request.setPageSize(100L);
        request.setCateId(categoryId);

        GetCategoriesResponse response = acs.getAcsResponse(request);
        if ((categoryId == null || categoryId == -1) && !StringUtils.isEmpty(vodProperties.getRootCategoryPrefix())) {
            for (GetCategoriesResponse.Category subCategory : response.getSubCategories()) {
                subCategory.setCateName(subCategory.getCateName().replaceFirst(vodProperties.getRootCategoryPrefix(), ""));
            }
        }
        return response;
    }

    @SneakyThrows
    public AddCategoryResponse addCategory(Long parentId, String categoryName) {
        AddCategoryRequest request = new AddCategoryRequest();
        request.setParentId(parentId);
        request.setCateName(categoryName);

        if ((parentId == null || parentId == -1) && !StringUtils.isEmpty(vodProperties.getRootCategoryPrefix())) {
            request.setCateName(vodProperties.getRootCategoryPrefix() + request.getCateName());
        }

        return acs.getAcsResponse(request);
    }

    @SneakyThrows
    public UpdateVideoInfosResponse updateVideosCategory(List<String> videoIds, Long categoryId) {
        UpdateVideoInfosRequest request = new UpdateVideoInfosRequest();
        List<Map<String, Object>> content = new ArrayList<>();
        for (String videoId : videoIds) {
            Map<String, Object> video = new HashMap<>();
            video.put("VideoId", videoId);
            video.put("CateId", categoryId);
            content.add(video);
        }
        request.setUpdateContent(JsonUtils.toJson(content));
        return acs.getAcsResponse(request);
    }


    @SneakyThrows
    public GetPlayInfoResponse playInfo(String videoId) {
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        request.setVideoId(videoId);
        return acs.getAcsResponse(request);
    }

    public DefaultAcsClient getAcsClient() {
        return acs;
    }

}
