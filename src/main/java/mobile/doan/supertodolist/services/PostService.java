package mobile.doan.supertodolist.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqCreatePostDTO;
import mobile.doan.supertodolist.dto.response.ResPaginationDTO;
import mobile.doan.supertodolist.model.Post;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.repository.PostRepository;
import mobile.doan.supertodolist.util.SecurityUtil;
import mobile.doan.supertodolist.util.error.AppException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    SecurityUtil securityUtil;
    UserService userService;

    public Post createPost(ReqCreatePostDTO req) throws AppException {
        String email = securityUtil.getCurrentUserLogin().orElseThrow(() -> new AppException("User not found"));
        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }
        Post post = new Post();
        post.setImage(req.getImage());
        post.setContent(req.getContent());
        post.setUser(user);
        return postRepository.save(post);
    }

    public ResPaginationDTO getAllPostWithPagination(
            Specification<Post> spec, Pageable pageable) {
        Page<Post> pagePosts = this.postRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pagePosts.getTotalPages());
        meta.setTotal(pagePosts.getTotalElements());
        rs.setMeta(meta);
        List<Post> listPosts = pagePosts.getContent();
        rs.setResult(listPosts);
        return rs;
    }

    public Post getPostById(Long id) throws AppException {
        Optional<Post> post = this.postRepository.findById(id);
        if (!post.isPresent()) {
            throw new AppException("Post not found");
        }
        return post.get();
    }

}
