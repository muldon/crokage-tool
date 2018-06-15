package com.ufu.bot.to;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity
@Table(name = "postsmin")
public class Post {
	private static final long serialVersionUID = -111652190111815641L;
	@Id
    private Integer id;
	
	private String body;
	
	private String title;
	
	 private String tags;
    	
	@Column(name="posttypeid")
	private Integer postTypeId;
	
	@Column(name="acceptedanswerid")
	private Integer acceptedAnswerId;
	
	@Column(name="parentid")
	private Integer parentId;
	
	@Column(name="creationdate")
	private Timestamp creationDate;
	
	@Column(name="score")
	private Integer score;
		
	@Column(name="viewcount")
	private Integer viewCount;
	    	
	@Column(name="owneruserid")
	private Integer ownerUserId;
	
	
	@Column(name="lasteditoruserid")
	private Integer lastEditorUserId;
	
	@Column(name="lasteditordisplayname")
    private String lastEditorDisplayName;
	
	@Column(name="lasteditdate")
	private Timestamp lastEditDate;
	
	@Column(name="lastactivitydate")
	private Timestamp lastActivityDate;
		
	@Column(name="answercount")
	private Integer answerCount;
	
	@Column(name="commentcount")
	private Integer commentCount;
	
	@Column(name="favoritecount")
	private Integer favoriteCount;
	
	@Column(name="closeddate")
	private Timestamp closedDate;
	
	@Column(name="communityowneddate")
	private Timestamp communityOwnedDate;
	
	
	@Transient
	private List<Comment> comments;  //lista de ids de posts com maior similaridade ( maior para menor )
	
	
	@Transient
	private User user;
	
	@Transient
	private String titleVectors,tagVectors,bodyVectors, topicVectors;
	
	@Transient
	private double similarityScore;
	
	@Transient
	private Set<String> classesNames;
	
	@Transient
	private ArrayList<Post> topKrelatedQuestions;  //lista de ids de posts com maior similaridade ( maior para menor )
	
	/*@Column(name="tagssyn")
	private String tagsSyn;
	
	private String code; */
	

	public Post() {
	}
	
	public Post(Integer id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleVectors() {
		return titleVectors;
	}

	public void setTitleVectors(String titleVectors) {
		this.titleVectors = titleVectors;
	}

	public String getTagVectors() {
		return tagVectors;
	}

	public void setTagVectors(String tagVectors) {
		this.tagVectors = tagVectors;
	}

	public String getBodyVectors() {
		return bodyVectors;
	}

	public void setBodyVectors(String bodyVectors) {
		this.bodyVectors = bodyVectors;
	}

	public String getTopicVectors() {
		return topicVectors;
	}

	public void setTopicVectors(String topicVectors) {
		this.topicVectors = topicVectors;
	}

	public double getSimilarityScore() {
		return similarityScore;
	}

	public void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}

	public ArrayList<Post> getTopKrelatedQuestions() {
		return topKrelatedQuestions;
	}

	public void setTopKrelatedQuestions(ArrayList<Post> topKrelatedQuestions) {
		this.topKrelatedQuestions = topKrelatedQuestions;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	

	public Integer getPostTypeId() {
		return postTypeId;
	}

	public void setPostTypeId(Integer postTypeId) {
		this.postTypeId = postTypeId;
	}

	public Integer getAcceptedAnswerId() {
		return acceptedAnswerId;
	}

	public void setAcceptedAnswerId(Integer acceptedAnswerId) {
		this.acceptedAnswerId = acceptedAnswerId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public Integer getOwnerUserId() {
		return ownerUserId;
	}

	public void setOwnerUserId(Integer ownerUserId) {
		this.ownerUserId = ownerUserId;
	}

	public Integer getLastEditorUserId() {
		return lastEditorUserId;
	}

	public void setLastEditorUserId(Integer lastEditorUserId) {
		this.lastEditorUserId = lastEditorUserId;
	}

	public String getLastEditorDisplayName() {
		return lastEditorDisplayName;
	}

	public void setLastEditorDisplayName(String lastEditorDisplayName) {
		this.lastEditorDisplayName = lastEditorDisplayName;
	}

	public Timestamp getLastEditDate() {
		return lastEditDate;
	}

	public void setLastEditDate(Timestamp lastEditDate) {
		this.lastEditDate = lastEditDate;
	}

	public Timestamp getLastActivityDate() {
		return lastActivityDate;
	}

	public void setLastActivityDate(Timestamp lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}

	public Integer getAnswerCount() {
		return answerCount;
	}

	public void setAnswerCount(Integer answerCount) {
		this.answerCount = answerCount;
	}

	public Integer getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}

	public Integer getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(Integer favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public Timestamp getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Timestamp closedDate) {
		this.closedDate = closedDate;
	}

	public Timestamp getCommunityOwnedDate() {
		return communityOwnedDate;
	}

	public void setCommunityOwnedDate(Timestamp communityOwnedDate) {
		this.communityOwnedDate = communityOwnedDate;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return "Post [id=" + id + ", body=" + body + ", title=" + title + ", tags=" + tags + ", postTypeId=" + postTypeId + ", parentId=" + parentId + ", creationDate=" + creationDate + ", score="
				+ score + ", ownerUserId=" + ownerUserId + ", answerCount=" + answerCount + ", commentCount=" + commentCount + ", user=" + user + "]";
	}

	public Set<String> getClassesNames() {
		return classesNames;
	}

	public void setClassesNames(Set<String> classesNames) {
		this.classesNames = classesNames;
	}

	
	

	
	
    
}