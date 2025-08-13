//package com.phungquocthai.symphony.dto;
//
//import java.time.LocalDateTime;
//
//import com.phungquocthai.symphony.entity.Comment;
//import jakarta.validation.constraints.NotNull;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Getter @Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class CommentDTO {
//
//	@NotNull(message = "Id không được để trống")
//    private Integer commentId;
//	
//	@NotNull(message = "Vui lòng nhập nội dung")
//	private String content;
//	
//	@NotNull(message = "Vui lòng cho biết thời gian bình luận")
//	private LocalDateTime timeComment;
//	
//    private Integer parentCommentId;
//
//	@NotNull(message = "Vui lòng điền thông tin người bình luận")
//    private UserCommentDTO user;
//	
//	public CommentDTO(Comment comment) {
//		this.commentId = comment.getComment_id();
//		this.content = comment.getContent();
//		this.timeComment = comment.getTime_comment();
//		this.parentCommentId = comment.getParent_comment().getComment_id();
//		this.user = new UserCommentDTO(comment.getUser());
//	}
//}
