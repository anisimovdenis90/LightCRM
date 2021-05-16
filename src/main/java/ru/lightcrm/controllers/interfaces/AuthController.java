package ru.lightcrm.controllers.interfaces;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.lightcrm.utils.JwtRequest;
import ru.lightcrm.utils.JwtResponse;

@Api(value = "Авторизация")
public interface AuthController {

  @PostMapping("/auth")
  @ApiOperation(value = " Возвращает JwtResponse с токеном",
      notes = " Проверяет, что пользователь ранее зарегистрирован в приложении и возвращает объект, содержащий токен авторизации",
      httpMethod = "POST"
  )
  @ApiResponses(value = {
      @ApiResponse(
          code = 200,
          message = "",
          response = JwtResponse.class
      ),
      @ApiResponse(
          code = 401,
          message = "Incorrect username or password OR Account deleted"
      )
  })
  ResponseEntity<?> createAuthToken(
      @ApiParam(value = "Объект запроса на авторизацию", required = true) @RequestBody JwtRequest jwtRequest);
}
