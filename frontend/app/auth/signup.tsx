/* eslint-disable @typescript-eslint/no-unused-vars */
import { View, Text, TouchableOpacity, ScrollView } from 'react-native';
import { useForm, Controller } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import { signupSchema } from 'utils/signupSchema';
import InputField from 'components/InputField';
import Button from 'components/Button';
import { useRouter } from 'expo-router';
import { FontAwesome } from '@expo/vector-icons';
import Toast from 'react-native-toast-message';
import { userService, UserRegistrationData } from 'services/userService';
import { useState } from 'react';

const SignupScreen = () => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const {
    control,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(signupSchema),
  });

  const onSubmit = async (data: UserRegistrationData) => {
    setIsLoading(true);
    try {
      await userService.register(data);
      console.log(data);

      Toast.show({
        type: 'success',
        text1: 'Signup Successful!',
        text2: 'Log into your account to continue.',
      });
      router.replace('/auth/login');
    } catch (error) {
      Toast.show({
        type: 'error',
        text1: 'Registration Failed',
        text2: 'Please try again later.',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <ScrollView
      className="flex-1 bg-[#F9F9FF] px-6"
      contentContainerStyle={{ justifyContent: 'center', flexGrow: 1 }}>
      <Text className="mb-6 text-center text-2xl font-bold text-text">Sign Up</Text>

      <Controller
        control={control}
        name="firstName"
        render={({ field: { onChange, value } }) => (
          <InputField
            label="First Name"
            placeholder="Enter your first name"
            value={value}
            onChangeText={onChange}
            iconName="user"
            error={errors.firstName?.message}
          />
        )}
      />

      <Controller
        control={control}
        name="lastName"
        render={({ field: { onChange, value } }) => (
          <InputField
            label="Last Name"
            placeholder="Enter your last name"
            value={value}
            onChangeText={onChange}
            iconName="user"
            error={errors.lastName?.message}
          />
        )}
      />

      <Controller
        control={control}
        name="username"
        render={({ field: { onChange, value } }) => (
          <InputField
            label="Email"
            placeholder="Enter your email"
            value={value}
            onChangeText={onChange}
            iconName="mail"
            error={errors.username?.message}
          />
        )}
      />

      <Controller
        control={control}
        name="password"
        render={({ field: { onChange, value } }) => (
          <InputField
            label="Password"
            placeholder="Enter your password"
            value={value}
            onChangeText={onChange}
            iconName="lock"
            isPassword
            error={errors.password?.message}
          />
        )}
      />

      <Button
        title="Sign Up"
        onPress={handleSubmit(onSubmit)}
        className="rounded-lg bg-primary-dark py-4"
        loading={isLoading}
        disabled={isLoading}
      />

      <Text className="mt-6 text-center text-sm text-text">
        Already have an account?{' '}
        <Text className="font-bold text-text" onPress={() => router.replace('/auth/login')}>
          Login
        </Text>
      </Text>

      {/* <View className="mt-4 flex-row justify-center gap-4 space-x-6">
        <TouchableOpacity className="flex h-[50px] w-[50px] items-center justify-center rounded-lg bg-gray-400 p-3">
          <FontAwesome name="google" size={24} color="#DB4437" />
        </TouchableOpacity>
        <TouchableOpacity className="flex h-[50px] w-[50px] items-center justify-center rounded-lg bg-gray-400 p-3">
          <FontAwesome name="facebook" size={24} color="#4267B2" />
        </TouchableOpacity>
      </View> */}
    </ScrollView>
  );
};

export default SignupScreen;
