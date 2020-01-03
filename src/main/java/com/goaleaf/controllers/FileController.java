package com.goaleaf.controllers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.goaleaf.entities.DTO.UserDTO;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.EditImageViewModel;
import com.goaleaf.security.uploadingFiles.FileStorageProperties;
import com.goaleaf.services.JwtService;
import com.goaleaf.services.PostService;
import com.goaleaf.services.UserService;
import com.goaleaf.services.servicesImpl.FileStorageService;
import com.goaleaf.validators.FileConverter;
import com.goaleaf.validators.exceptions.FilesStorage.FormatNotAllowedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.charset.StandardCharsets;

import static com.goaleaf.security.SecurityConstants.SECRET;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    private FileStorageProperties fileStorageProperties = new FileStorageProperties();
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadImage")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public String uploadProfilePic(@RequestParam("file") MultipartFile file, @RequestParam("token") String token) {
        if (!jwtService.Validate(token, SECRET))
            throw new TokenExpiredException("You have to be logged in to send a photo!");

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token).getBody();

        EditImageViewModel edit = new EditImageViewModel();
        edit.id = Integer.parseInt(claims.getSubject());

        String allowedExtentions = "image/jpg,image/jpeg,image/png,image/gif";
        String substring = file.getContentType();

        if (!allowedExtentions.contains(substring))
            throw new FormatNotAllowedException("Wrong file format!");

        java.io.File result = userService.uploadProfileImage(file, token);
//        return Response.ok(result, MediaType.APPLICATION_OCTET_STREAM)
//                .header("Content-Disposition", "attachment; filename=\"" + result.getName() + "\"") //optional
//                .build();
        UserDTO user = userService.findById(Integer.parseInt(claims.getSubject()));
        return user.getImageCode();

    }

    @GetMapping("/getProfilePic")
    public Response getUserProfilePicture(Integer userID) {
        File result = userService.getProfilePicture(userID);
        return Response.ok(result, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + result.getName() + "\"") //optional
                .build();
    }

    @GetMapping("/getPostPic")
    public Response getPostPicture(Integer postID) {
        File result = postService.getPostPicture(postID);
        return Response.ok(result, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + result.getName() + "\"") //optional
                .build();
    }

    @GetMapping("/encode")
    public String encodeFileToBase64(java.io.File file) {
        return FileConverter.encodeFileToBase64Binary(file);
    }

    @GetMapping("/decode")
    public Response decodeFileFromBase64(String base64) {
        File result = FileConverter.decodeFileFromBase64Binary(base64);
        return Response.ok(result, MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=\"" + result.getName() + "\"") //optional
                .build();
    }

    @GetMapping("/getProfilePicString")
    public String getProfilePictureBaseString(@RequestParam Integer userID) {
        return userService.getUserImageCode(userID);
    }

    @GetMapping("/getPostPicString")
    public String getPostPictureBaseString(@RequestParam Integer postID) {
        return postService.getPostImageCode(postID);
    }
}