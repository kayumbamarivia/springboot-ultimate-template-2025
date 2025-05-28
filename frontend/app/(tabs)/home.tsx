import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  RefreshControl,
  ActivityIndicator,
} from 'react-native';
import { useRouter } from 'expo-router';
import { FontAwesome } from '@expo/vector-icons';
import { useUser } from '../../contexts/UserContext';
import { useState, useCallback, useEffect } from 'react';
import ExpenseFormModal from '../../components/ExpenseFormModal';
import { expenseService } from '../../services/expenseService';
import { Expense } from '../../types/expense';
import Toast from 'react-native-toast-message';

export default function HomeScreen() {
  const router = useRouter();
  const { user } = useUser();
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [refreshing, setRefreshing] = useState(false);
  const [loading, setLoading] = useState(true);

  const fetchExpenses = useCallback(async () => {
    if (!user) {
      setLoading(false);
      return;
    }

    try {
      const data = await expenseService.getAllExpenses(user.id);
      setExpenses(Array.isArray(data) ? data : []); // Ensure data is an array
    } catch (error) {
      Toast.show({
        type: 'error',
        text1: 'Error',
        text2: 'Failed to fetch expenses',
      });
    } finally {
      setLoading(false);
    }
  }, [user]);

  const onRefresh = useCallback(async () => {
    setRefreshing(true);
    await fetchExpenses();
    setRefreshing(false);
  }, [fetchExpenses]);

  useEffect(() => {
    fetchExpenses();
  }, [fetchExpenses]);

  // Calculate financial overview
  const calculateFinancialOverview = () => {
    if (!expenses || expenses.length === 0) {
      return {
        total: 0,
        thisMonth: 0,
        lastMonth: 0,
      };
    }

    const now = new Date();
    const currentMonth = now.getMonth();
    const currentYear = now.getFullYear();
    const lastMonth = currentMonth === 0 ? 11 : currentMonth - 1;
    const lastMonthYear = currentMonth === 0 ? currentYear - 1 : currentYear;

    const totalExpenses = expenses.reduce((sum, expense) => sum + expense.amount, 0);

    const thisMonthExpenses = expenses
      .filter((expense) => {
        const expenseDate = new Date(expense.date);
        return expenseDate.getMonth() === currentMonth && expenseDate.getFullYear() === currentYear;
      })
      .reduce((sum, expense) => sum + expense.amount, 0);

    const lastMonthExpenses = expenses
      .filter((expense) => {
        const expenseDate = new Date(expense.date);
        return expenseDate.getMonth() === lastMonth && expenseDate.getFullYear() === lastMonthYear;
      })
      .reduce((sum, expense) => sum + expense.amount, 0);

    return {
      total: totalExpenses,
      thisMonth: thisMonthExpenses,
      lastMonth: lastMonthExpenses,
    };
  };

  const financialOverview = calculateFinancialOverview();
  const recentExpenses = [...expenses]
    .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime())
    .slice(0, 5);

  if (!user) {
    return (
      <View className="flex-1 items-center justify-center bg-[#F9F9FF]">
        <Text className="text-lg text-gray-600">Please log in to view your dashboard</Text>
      </View>
    );
  }

  return (
    <ScrollView
      className="flex-1 bg-[#F9F9FF]"
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}>
      {/* Header */}
      <View className="bg-primary-dark px-6 pb-6 pt-12">
        <Text className="text-2xl font-bold text-white">Welcome back,</Text>
        <Text className="text-xl text-white/80">{user.firstName}!</Text>
      </View>

      {/* Quick Actions */}
      <View className="px-6 py-6">
        <Text className="mb-4 text-lg font-semibold text-gray-800">Quick Actions</Text>
        <View className="flex-row flex-wrap gap-4">
          <TouchableOpacity
            onPress={() => setIsModalVisible(true)}
            className="min-w-[150px] flex-1 rounded-xl bg-white p-4 shadow-sm">
            <View className="mb-2 h-12 w-12 items-center justify-center rounded-full bg-primary-light">
              <FontAwesome name="plus" size={20} color="#4F46E5" />
            </View>
            <Text className="font-semibold text-gray-800">Add Expense</Text>
            <Text className="text-sm text-gray-500">Record new spending</Text>
          </TouchableOpacity>

          <TouchableOpacity
            onPress={() => router.push('/expenses')}
            className="min-w-[150px] flex-1 rounded-xl bg-white p-4 shadow-sm">
            <View className="mb-2 h-12 w-12 items-center justify-center rounded-full bg-green-100">
              <FontAwesome name="list" size={20} color="#059669" />
            </View>
            <Text className="font-semibold text-gray-800">View Expenses</Text>
            <Text className="text-sm text-gray-500">See all transactions</Text>
          </TouchableOpacity>
        </View>
      </View>

      {/* Financial Overview */}
      <View className="px-6 py-6">
        <Text className="mb-4 text-lg font-semibold text-gray-800">Financial Overview</Text>
        <View className="rounded-xl bg-white p-6 shadow-sm">
          {loading ? (
            <View className="items-center justify-center py-8">
              <ActivityIndicator size="large" color="#4F46E5" />
            </View>
          ) : (
            <>
              <View className="mb-6">
                <Text className="text-sm text-gray-500">Total Expenses</Text>
                <Text className="text-2xl font-bold text-gray-800">
                  ${financialOverview.total.toFixed(2)}
                </Text>
              </View>
              <View className="flex-row justify-between">
                <View>
                  <Text className="text-sm text-gray-500">This Month</Text>
                  <Text className="text-lg font-semibold text-gray-800">
                    ${financialOverview.thisMonth.toFixed(2)}
                  </Text>
                </View>
                <View>
                  <Text className="text-sm text-gray-500">Last Month</Text>
                  <Text className="text-lg font-semibold text-gray-800">
                    ${financialOverview.lastMonth.toFixed(2)}
                  </Text>
                </View>
              </View>
            </>
          )}
        </View>
      </View>

      {/* Recent Activity */}
      <View className="px-6 py-6">
        <Text className="mb-4 text-lg font-semibold text-gray-800">Recent Activity</Text>
        <View className="rounded-xl bg-white p-6 shadow-sm">
          {loading ? (
            <View className="items-center justify-center py-8">
              <ActivityIndicator size="large" color="#4F46E5" />
            </View>
          ) : recentExpenses.length === 0 ? (
            <View className="items-center justify-center  py-8">
              <FontAwesome name="history" size={48} color="#9CA3AF" />
              <Text className="mt-4 text-center text-gray-500">No recent activity</Text>
              <Text className="mt-2 text-center text-sm text-gray-400">
                Your recent transactions will appear here
              </Text>
            </View>
          ) : (
            recentExpenses.map((expense) => (
              <TouchableOpacity
                key={expense.id}
                onPress={() => router.push(`/expense/${expense.id}`)}
                className="mb-4 border-b border-gray-100 pb-4 last:mb-0 last:border-0 last:pb-0">
                <View className="flex-row items-center justify-between">
                  <View className="flex-row items-center">
                    <View className="mr-3 h-10 w-10 items-center justify-center rounded-full bg-primary-light">
                      <FontAwesome name="money" size={16} color="#4F46E5" />
                    </View>
                    <View>
                      <Text className="font-semibold text-gray-800">{expense.name}</Text>
                      <Text className="text-sm text-gray-500">{expense.category}</Text>
                    </View>
                  </View>
                  <View className="m-4 items-end">
                    <Text className="font-semibold text-gray-800">
                      ${expense.amount.toFixed(2)}
                    </Text>
                    <Text className="text-sm text-gray-500">
                      {new Date(expense.date).toLocaleDateString()}
                    </Text>
                  </View>
                </View>
              </TouchableOpacity>
            ))
          )}
        </View>
      </View>

      {/* Expense Form Modal */}
      <ExpenseFormModal
        visible={isModalVisible}
        onClose={() => setIsModalVisible(false)}
        onSuccess={() => {
          setIsModalVisible(false);
          fetchExpenses();
        }}
      />
    </ScrollView>
  );
}
