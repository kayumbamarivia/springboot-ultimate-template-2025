import { View, Text, ScrollView, TouchableOpacity, RefreshControl, ActivityIndicator } from 'react-native';
import { useState, useCallback, useEffect } from 'react';
import { useRouter } from 'expo-router';
import { FontAwesome } from '@expo/vector-icons';
import Toast from 'react-native-toast-message';
import { expenseService } from '../../services/expenseService';
import { Expense } from '../../types/expense';
import { useUser } from '../../contexts/UserContext';
import ExpenseFormModal from '../../components/ExpenseFormModal';
import { EXPENSE_CATEGORIES, ExpenseCategory } from '../../utils/constants';

export default function ExpensesScreen() {
  const router = useRouter();
  const { user } = useUser();
  const [expenses, setExpenses] = useState<Expense[]>([]);
  const [refreshing, setRefreshing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState<ExpenseCategory | 'all'>('all');

  const fetchExpenses = useCallback(async () => {
    if (!user) {
      setLoading(false);
      return;
    }

    try {
      const data = await expenseService.getAllExpenses(user.id);
      setExpenses(data);
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

  const handleAddExpense = () => {
    setIsModalVisible(true);
  };

  const handleExpensePress = (expenseId: string) => {
    router.push(`/expense/${expenseId}`);
  };

  const filteredExpenses =
    selectedCategory === 'all'
      ? expenses
      : expenses.filter((expense) => expense.category === selectedCategory);

  if (!user) {
    return (
      <View className="flex-1 items-center justify-center bg-[#F9F9FF]">
        <Text className="text-lg text-gray-600">Please log in to view your expenses</Text>
      </View>
    );
  }

  return (
    <View className="h-full bg-[#F9F9FF]">
      {/* Header */}
      <View className="flex flex-row items-center justify-between bg-primary-dark px-6 pb-6 pt-12">
        <View>
          <Text className="text-2xl font-bold text-white">My Expenses</Text>
          <Text className="text-white/80">Track your daily expenses</Text>
        </View>
        {/* Add Expense Button */}
        <TouchableOpacity
          onPress={handleAddExpense}
          className="h-10 w-10 rounded-full bg-white p-4 shadow-lg">
          <FontAwesome name="plus" size={8} color="#4F46E5" className="m-auto" />
        </TouchableOpacity>
      </View>

      {/* Category Filter */}
      <View className="bg-white">
        <ScrollView horizontal showsHorizontalScrollIndicator={false} className=" py-2">
          <TouchableOpacity
            onPress={() => setSelectedCategory('all')}
            className={` mx-1 rounded-full px-4 py-2 ${selectedCategory === 'all' ? 'bg-primary-dark' : 'bg-gray-100'}`}
            style={{ height: 36 }}>
            <Text
              className={`font-medium ${
                selectedCategory === 'all' ? 'text-white' : 'text-gray-600'
              }`}>
              All
            </Text>
          </TouchableOpacity>
          {EXPENSE_CATEGORIES.map((category) => (
            <TouchableOpacity
              key={category.id}
              onPress={() => setSelectedCategory(category.id)}
              className={`mx-1 flex-row items-center rounded-full  px-4 py-2 ${selectedCategory === category.id ? 'bg-primary-dark' : 'bg-gray-100'}`}
              style={{ height: 36 }}>
              <FontAwesome
                name={category.icon}
                size={14}
                color={selectedCategory === category.id ? 'white' : '#4B5563'}
                className="mr-2"
              />
              <Text
                className={`font-medium ${
                  selectedCategory === category.id ? 'text-white' : 'text-gray-600'
                }`}>
                {category.label}
              </Text>
            </TouchableOpacity>
          ))}
        </ScrollView>
      </View>
      {/* Expenses List */}
      <ScrollView
        className="flex-1 px-6 pt-2"
        refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}>
        {loading ? (
          <View className="mt-8 items-center justify-center">
            <ActivityIndicator size="large" color="#4F46E5" />
          </View>
        ) : filteredExpenses.length === 0 ? (
          <View className="mt-8 items-center">
            <FontAwesome name="file-text-o" size={48} color="#9CA3AF" />
            <Text className="mt-4 text-center text-gray-500">No expenses found</Text>
            <Text className="mt-2 text-center text-gray-400">
              {selectedCategory === 'all'
                ? 'Tap the + button to add your first expense'
                : `No expenses in ${EXPENSE_CATEGORIES.find((c) => c.id === selectedCategory)?.label} category`}
            </Text>
          </View>
        ) : (
          filteredExpenses.map((expense) => (
            <TouchableOpacity
              key={expense.id}
              onPress={() => handleExpensePress(expense.id)}
              className="mb-4 rounded-lg bg-white p-4 shadow-sm">
              <View className="flex-row items-center justify-between">
                <View className="flex-row items-center">
                  <View className="mr-3 h-10 w-10 items-center justify-center rounded-full bg-primary-light">
                    <FontAwesome
                      name={
                        EXPENSE_CATEGORIES.find((c) => c.id === expense.category)?.icon || 'money'
                      }
                      size={16}
                      color="#4F46E5"
                    />
                  </View>
                  <View>
                    <Text className="text-lg font-semibold text-gray-800">{expense.name}</Text>
                    <Text className="text-sm text-gray-500">
                      {EXPENSE_CATEGORIES.find((c) => c.id === expense.category)?.label ||
                        expense.category}
                    </Text>
                  </View>
                </View>
                <Text className="text-lg font-bold text-primary-dark">
                  ${Number(expense.amount).toFixed(2)}
                </Text>
              </View>
              <Text className="mt-2 text-sm text-gray-500">
                {new Date(expense.date).toLocaleDateString()}
              </Text>
            </TouchableOpacity>
          ))
        )}
      </ScrollView>

      {/* Expense Form Modal */}
      <ExpenseFormModal
        visible={isModalVisible}
        onClose={() => setIsModalVisible(false)}
        onSuccess={fetchExpenses}
      />
    </View>
  );
}
