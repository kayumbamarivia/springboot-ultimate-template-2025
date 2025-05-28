import { View, Text } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Link, router } from 'expo-router';
import Button from 'components/Button';
import '../global.css'


export default function LandingPage() {
  return (
    <SafeAreaView className="flex-1 bg-white">
      <View className="flex-1 p-5 justify-between">
        <View className="items-center mt-10">
          <Text className="text-4xl font-bold text-gray-800 mb-2">FinTrack</Text>
          <Text className="text-lg text-gray-600 text-center">
            Your Personal Finance Companion
          </Text>
        </View>

        <View className="my-10">
          <View className="mb-6 p-5 bg-gray-50 rounded-xl">
            <Text className="text-xl font-semibold text-gray-800 mb-2">
              Track Expenses
            </Text>
            <Text className="text-base text-gray-600 leading-6">
              Monitor your spending habits and stay within budget
            </Text>
          </View>

          <View className="mb-6 p-5 bg-gray-50 rounded-xl">
            <Text className="text-xl font-semibold text-gray-800 mb-2">
              Smart Analytics
            </Text>
            <Text className="text-base text-gray-600 leading-6">
              Get insights into your financial patterns
            </Text>
          </View>

          <View className="mb-6 p-5 bg-gray-50 rounded-xl">
            <Text className="text-xl font-semibold text-gray-800 mb-2">
              Secure & Private
            </Text>
            <Text className="text-base text-gray-600 leading-6">
              Your data is encrypted and protected
            </Text>
          </View>
        </View>

        <View className="gap-3 mb-5">
            <Button
              title="Get Started"
              onPress={() => {router.replace('/auth/signup')}}
              className="bg-blue-500"
            />

            <Button
              title="Login"
              onPress={() => {router.replace('/auth/login')}}
              className="bg-gray-100"
              textClassName="font-semibold text-center text-gray-800"
            />
        </View>
      </View>
    </SafeAreaView>
  );
}
