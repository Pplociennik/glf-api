package com.goaleaf.controllers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.EditImageViewModel;
import com.goaleaf.security.uploadingFiles.FileStorageProperties;
import com.goaleaf.security.uploadingFiles.UploadFileResponse;
import com.goaleaf.services.JwtService;
import com.goaleaf.services.PostService;
import com.goaleaf.services.UserService;
import com.goaleaf.services.servicesImpl.FileStorageService;
import com.goaleaf.validators.exceptions.FilesStorage.FormatNotAllowedException;
import com.goaleaf.validators.exceptions.FilesStorage.WrongUploadFileProcessTypeException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.goaleaf.security.SecurityConstants.SECRET;

@RestController
public class FileController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;

    private FileStorageProperties fileStorageProperties = new FileStorageProperties();

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadImage")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("token") String token, @RequestParam("type") String type, Integer postID) {
        if (!jwtService.Validate(token, SECRET))
            throw new TokenExpiredException("You have to be logged in to send a photo!");
        if (!Objects.equals(type, "PROFILE") && !Objects.equals(type, "POST"))
            throw new WrongUploadFileProcessTypeException("UNKNOWN__PROCESS_TYPE");

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token).getBody();

        EditImageViewModel edit = new EditImageViewModel();
        edit.id = Integer.parseInt(claims.getSubject());

        String allowedExtentions = "image/jpg,image/jpeg,image/png,image/gif";
        String substring = file.getContentType();

        if (!allowedExtentions.contains(substring))
            throw new FormatNotAllowedException("Wrong file format!");

        String fileName = "";
        if (Objects.equals(type, "PROFILE"))
            fileName = fileStorageService.storeFile(file, Integer.parseInt(claims.getSubject()), type);
        else
            fileName = fileStorageService.storeFile(file, postID, type);

        if (Objects.equals(type, "PROFILE")) {
            edit.imageName = fileName;
            userService.updateUserImage(edit);
        } else {
            postService.updatePostImage(postID, fileName);
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

//    @PostMapping("/uploadMultipleFiles")
//    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
//        return Arrays.asList(files)
//                .stream()
//                .map(file -> uploadFile(file))
//                .collect(Collectors.toList());
//    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(Integer userID, Integer postID, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = null;
        if (postID == null)
            resource = fileStorageService.loadFileAsResource(userService.findById(userID).getImageName());
        else
            resource = fileStorageService.loadFileAsResource(postService.findOneByID(postID).getImgName());

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
