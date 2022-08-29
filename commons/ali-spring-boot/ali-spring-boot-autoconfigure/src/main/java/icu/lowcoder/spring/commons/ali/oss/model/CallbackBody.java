package icu.lowcoder.spring.commons.ali.oss.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CallbackBody {
    private String bucket;//	存储空间
    private String object;//	对象（文件）
    private String etag;//	文件的 ETtag，即返回给用户的 ETag 字段
    private Long size;//	Object 大小，CompleteMultipartUpload 时为整个Object 的大小
    private String mimeType;//	资源类型，如 jpeg 图片的资源类型为 image/jpeg

    // 图片数据，可能为空
    private String imageWidth;
    private String imageHeight;
    private String imageFormat; // 	图片格式，如 jpg、png 等

    // 自定义参数
    private String userId;
    private String dir;
    private String originalName;
    private String host;
}
