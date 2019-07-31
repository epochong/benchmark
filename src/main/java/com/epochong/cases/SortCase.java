package com.epochong.cases;

import com.epochong.Case;
import com.epochong.annotation.Benchmark;
import com.epochong.annotation.Measurement;

import java.util.Arrays;
import java.util.Random;

/**
 * @author epochong
 * @date 2019/7/13 10:01
 * @email epochong@163.com
 * @blog epochong.github.io
 * @describe
 */

/**
 * 1.快速排序和归并的差别
 * 2.自己实现的归并排序 和Arrays.sort对比
 * 3.TODO: 自己实现并发排序(ForkJoin)和Arrays.parallelSort
 */
@Measurement(iterations = 10, group = 3)
public class SortCase implements Case {

    public static void quickSort(int[] arr) {
        if (arr.length < 2) {
            return;
        }
        quickSort(arr,0,arr.length - 1);
    }
    public static void quickSort(int[] arr, int left, int right) {
        if (left < right) {
            swap(arr,right, left + (int) (Math.random() * left - right + 1));
            int[] p = partation(arr,left,right);
            quickSort(arr,0,p[0]);
            quickSort(arr,p[1],right);
        }
    }
    public static int[] partation(int[] arr, int left, int right) {
        int less = left - 1;
        int more = right;
        while (left < more) {
            if (arr[left] < arr[right]) {
                swap (arr,++less,left++);
            } else if (arr[left] > arr[right]) {
                swap(arr,left,--more);
            } else {
                left++;
            }
        }
        swap(arr,more++,right);
        return new int[]{less,more};
    }

    public static void mergeSort(int[] arr) {
        if (arr.length < 2) {
            return;
        }
        mergeSort(arr,0,arr.length - 1);
    }
    public static void mergeSort(int[] arr, int left, int right) {
        if (left == right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr,left,mid);
        mergeSort(arr,mid,right);
        merge(arr,left,mid,right);

    }
    public static void merge(int[] arr, int left,int mid, int right) {
        int[] help = new int[right - left + 1];
        int p = left;
        int q = mid + 1;
        int i = 0;
        while (p <= mid && q <= right) {
            help[i++] = arr[p] < arr[q] ? arr[p++] : arr[q++];
        }
        while (p < mid) {
            help[i++] = arr[p++];
        }
        while (q < right) {
            help[i++] = arr[q++];
        }
        for (int j = 0; j < help.length; j++) {
            arr[left + j] = help[j];
        }
    }
    public static void swap(int[] arr, int i, int j) {
        int tem = arr[i];
        arr[i] = arr[j];
        arr[j] = tem;
    }
    @Benchmark
    public void testQuickSort() {
        int[] a = new int[100000];
        Random random = new Random(20190713);
        for (int i = 0; i < a.length; i++) {
            a[i] = random.nextInt(10000);
        }
        Arrays.sort(a);
    }
}
