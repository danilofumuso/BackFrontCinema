package it.epicode.marsiflexbe.users;

import it.epicode.marsiflexbe.auth.AppUser;
import it.epicode.marsiflexbe.auth.AppUserRepository;
import it.epicode.marsiflexbe.auth.UserResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppUserMapper {
    private ModelMapper modelMapper = new ModelMapper();
    private final AppUserRepository appUserRepository;

    public AppUserMapper(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    // fatta usando la beanutils
    public UserResponse toLoginUserResponseBU(AppUser appUser) {
       UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(appUser, userResponse);
        userResponse.setPassword("");
        userResponse.setEmail(appUser.getUsername() );
        return userResponse;
    }

    // ModelMapper
    public UserResponse toLoginUserResponse(AppUser appUser) {
        UserResponse userResponse = modelMapper.map(appUser, UserResponse.class);
        userResponse.setPassword("");
        userResponse.setEmail(appUser.getUsername() );

        return userResponse;
    }

    // prende una lista di AppUser e la trasforma in una lista di loginUserResponse
    public List<UserResponse> toLoginUserResponseList(List<AppUser> appUsers) {
        return appUsers
                .stream()
                .map(this::toLoginUserResponse)
                .toList();
    }

}
