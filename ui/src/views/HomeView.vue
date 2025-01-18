<template>
  <VPageHeader title="API配置存储">
    <template #actions>
      <VButton type="secondary" @click="showModal">新增</VButton>
    </template>
  </VPageHeader>

  <VModal
    :visible="modalVisible"
    title="API配置存储"
    @close="handleCloseModal"
    layerClosable
    mount-to-body
  >
    <FormKit
      type="text"
      label="备注"
      name="remark"
      placeholder="请输入备注"
      v-model="currentConfig.spec.remark"
      validation="required"
    />
    <FormKit
      type="text"
      label="标识"
      name="identifier"
      placeholder="请输入标识（用于前端调用）"
      v-model="currentConfig.spec.identifier"
      validation="required|alphanumeric"
      :disabled="currentConfig.identifierDisabled"
    />

    <FormKit
      type="text"
      label="API 地址"
      name="apiAddress"
      placeholder="请输入要获取的 API 地址"
      v-model="currentConfig.spec.apiAddress"
      validation="required|starts_with:/"
      :validation-messages="{
       regex: 'API 地址必须以 / 开头的内部地址',
      }"
    />


    <FormKit
      type="hidden"
      name="apiData"
      :value="currentConfig.spec.apiData"
    />
    <VSpace>
      <VButton type="primary" @click="handleSubmit">提交</VButton>
      <VButton @click="handleCloseModal">取消</VButton>
    </VSpace>
  </VModal>

  <div class="m-0 md:m-4">

    <VCard :body-class="['!p-0']">

      <div v-if="configList.length === 0" class="p-4">
        <VEmpty title="当前数据为空" message="点击新建进行创建">
          <template #actions>
            <VSpace>
              <VButton type="secondary" @click="showModal">新建</VButton>
            </VSpace>
          </template>
        </VEmpty>
      </div>

      <Transition v-else appear name="fade">
        <ul class="box-border h-full w-full divide-y divide-gray-100" role="list">
          <li v-for="(item, index) in configList" :key="index">
            <VEntity :inert="modalVisible">
              <template #start>
                <VEntityField :title="item.spec.remark"></VEntityField>
              </template>
              <template #end>
                <VEntityField>
                  <template #description>
                    <VTag>{{ item.spec.identifier }}</VTag>
                  </template>
                </VEntityField>
                <VEntityField>
                  <template #description>
                    <span class="truncate text-xs tabular-nums text-gray-500">
                      {{ item.spec.apiAddress }}
                    </span>
                  </template>
                </VEntityField>
              </template>
              <template #dropdownItems>
                <VDropdownItem @click="handleEditItem(index)">编辑</VDropdownItem>
                <VDropdownItem @click="handleUpdateItem(index)">刷新json</VDropdownItem>
                <VDropdownItem @click="handlePreviewItem(index)">预览json</VDropdownItem>
                <div class="my-1 h-[1px] w-full bg-gray-100"></div>
                <VDropdownItem type="danger" @click="handleDeleteItem(index)">删除</VDropdownItem>
              </template>
            </VEntity>
          </li>
        </ul>
      </Transition>

      <template #footer>
        <VPagination
          v-model:page="currentPage"
          v-model:size="pageSize"
          :total="totalItems"
          @update:page="handlePageChange"
          @update:size="handleSizeChange"
        />
      </template>
    </VCard>


  </div>

  <VModal :visible="previewModalVisible" title="JSON 数据预览" @close="previewModalVisible = false">
    <pre>{{ formattedApiData }}</pre>
   
  </VModal>
</template>

<script setup lang="ts">
import {ref, watch, reactive, onMounted, shallowRef} from 'vue';

import { axiosInstance } from '@halo-dev/api-client';
import {
  VButton,
  VModal,
  VSpace,
  VPageHeader,
  VCard,
  VEntityField,
  VTag,
  VDropdownItem,
  VEntity,
  VEmpty, VPagination,Toast,
} from '@halo-dev/components';
import { FormKit } from '@formkit/vue';
import pinyin from 'pinyin';

interface ConfigItem {
  remark: string;
  identifier: string;
  apiAddress: string;
  apiData?: string; // 可选，用于存储从 API 获取的 JSON 数据
}

interface Config {
  apiVersion: string;
  kind: string;
  metadata: {
    name: string;
  };
  spec: ConfigItem;
}

interface ConfigPageResult {
  page: number;
  size: number;
  total: number;
  items: Config[];
  first: boolean;
  last: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  totalPages: number;
}

const modalVisible = ref(false);
// 当前页码
const currentPage = ref(1);
// 每页条数
const pageSize = ref(10);
// 总数据条数
const totalItems = ref(0);
// 是否有下一页
const hasNextPage = ref(false);
// 数据列表
const configList = ref<Config[]>([]);

// 当前新增的配置项，使用 reactive 创建响应式对象
const currentConfig = reactive<Config & { identifierDisabled?: boolean }>({
  apiVersion: "api-configstore.halo.run/v1alpha1", // 请替换为您插件的 actual group
  kind: "Config",
  metadata: {
    name: "",
  },
  spec: {
    remark: "",
    identifier: "",
    apiAddress: "",
    apiData: "",
  },
  identifierDisabled: false,
});



// 监听 remark 变化，自动生成 identifier
watch(
  () => currentConfig.spec.remark,
  (newVal) => {
    // 仅在模态框打开且非编辑状态下自动生成 identifier
    if (modalVisible.value === true && !currentConfig.identifierDisabled) {
      currentConfig.spec.identifier = pinyin(newVal, {
        style: pinyin.STYLE_NORMAL,
      }).join("");
    }
  }
);

// 获取配置列表数据
const fetchConfigList = async () => {
  try {
    const response: { data: ConfigPageResult } = await axiosInstance.get(
      `/apis/api-configstore.halo.run/v1alpha1/configs?page=${currentPage.value}&size=${pageSize.value}`
    );
    configList.value = response.data.items;
    totalItems.value = response.data.total;
    hasNextPage.value = response.data.hasNext;
  } catch (error) {
    console.error("获取配置列表失败:", error);
    Toast.error("获取配置列表失败", { duration: 5000 });
  }
};

// 页码发生变化时调用
const handlePageChange = (newPage: number) => {
  currentPage.value = newPage;
  fetchConfigList();
};

// 每页显示条数发生变化时调用
const handleSizeChange = (newSize: number) => {
  pageSize.value = newSize;
  currentPage.value = 1; // 重置页码为 1
  fetchConfigList();
};

// 显示模态框
const showModal = () => {
  // 重置 currentConfig 为初始状态，并启用 identifier 编辑
  Object.assign(currentConfig, {
    apiVersion: "api-configstore.halo.run/v1alpha1",
    kind: "Config",
    metadata: {
      name: "",
    },
    spec: {
      remark: "",
      identifier: "",
      apiAddress: "",
      apiData: "",
    },
    identifierDisabled: false,
  });
  modalVisible.value = true;
};

// 关闭模态框
const handleCloseModal = () => {
  modalVisible.value = false;
};

// 使用 shallowRef 创建一个对原始配置的浅层引用
const originalConfig = shallowRef<Config | null>(null);

// 编辑配置项
const handleEditItem = (index: number) => {
  const config = configList.value[index];
  // 深拷贝 config
  originalConfig.value = JSON.parse(JSON.stringify(config));
  // 复制选中的配置项到 currentConfig，并禁用 identifier 编辑
  Object.assign(currentConfig, config, {
    identifierDisabled: true,
  });
  modalVisible.value = true;
};

// 提交表单
const handleSubmit = async () => {
  try {
    if (currentConfig.identifierDisabled) {
      // 更新
      const response = await axiosInstance.get(currentConfig.spec.apiAddress);
      if (!response) {
        return;
      }
      // 更新 apiData
      currentConfig.spec.apiData = JSON.stringify(response.data);
      await axiosInstance.put(
        `/apis/api-configstore.halo.run/v1alpha1/configs/${currentConfig.metadata.name}`,
        currentConfig
      );
      Toast.success("更新成功", { duration: 5000 });
      console.log("更新成功");
    } else {
      // 新增
      const response = await axiosInstance.get(currentConfig.spec.apiAddress);
      if (!response) {
        return;
      }
      const config: Config = {
        apiVersion: "api-configstore.halo.run/v1alpha1",
        kind: "Config",
        metadata: {
          name: currentConfig.spec.identifier,
        },
        spec: {
          remark: currentConfig.spec.remark,
          identifier: currentConfig.spec.identifier,
          apiAddress: currentConfig.spec.apiAddress,
          apiData: JSON.stringify(response.data, null, 2),
        },
      };
      await axiosInstance.post(`/apis/api-configstore.halo.run/v1alpha1/configs`, config);
      Toast.success("提交成功", { duration: 5000 });
      console.log("提交成功");
    }
    handleCloseModal();
    await fetchConfigList();
  } catch (error) {
    Toast.error("数据保存或更新失败", { duration: 5000 });
    console.error("数据保存或更新失败:", error);
  }
};
// 更新json
const handleUpdateItem = async (index: number) => {
  const config = configList.value[index];
  try {
    const response = await axiosInstance.get(config.spec.apiAddress);

    await  axiosInstance.put(
      `/apis/api-configstore.halo.run/v1alpha1/configs/${config.metadata.name}`,
      {...config,spec:{...config.spec, apiData: JSON.stringify(response.data) }}
    );
    // 更新成功后，可以根据需要进行提示或其他操作
    Toast.success("JSON 数据刷新成功", {duration: 5000});
    console.log("JSON 数据刷新成功");
  } catch (error) {
    console.error("获取 API 数据失败:", error);
    Toast.error("获取 API 数据失败", {duration: 5000});
  } 
};



// 删除配置项
const handleDeleteItem = async (index: number) => {
  try {
    const name = configList.value[index].metadata.name;
    await axiosInstance.delete(`/apis/api-configstore.halo.run/v1alpha1/configs/${name}`);
    Toast.success("删除成功", {duration: 5000});
    setTimeout(() => {
      fetchConfigList();
    }, 800); // 延迟 800 毫秒后刷新列表
  } catch (error) {
    Toast.error("数据删除失败", {duration: 5000});
    console.error("数据删除失败:", error);
  }
};



const previewModalVisible = ref(false);
const formattedApiData = ref(""); 


// 预览
const handlePreviewItem = async (index: number) => {
  const config = configList.value[index];
  formattedApiData.value = ""; 
  previewModalVisible.value = true;
  try {
    const response = await axiosInstance.get(
      `/apis/api-configstore.halo.run/v1alpha1/configs/${config.metadata.name}`
    );
    const apiDataObj = JSON.parse(response.data.spec.apiData);
    if(!apiDataObj) {
      formattedApiData.value = ("数据为空");
    }else{
      formattedApiData.value = JSON.stringify(apiDataObj, null, 2);
    }
   

  } catch (error) {
    console.error("获取 API 数据失败:", error);
    formattedApiData.value = "获取 JSON 数据失败";
    Toast.error("获取 API 数据失败", {duration: 5000});
  } 
};

onMounted(fetchConfigList);
</script>
<style scoped>
pre {
  background-color: #f6f8fa; 
  border: 1px solid #d1d5da; 
  border-radius: 4px; 
  padding: 10px;
  font-size: 14px;
  line-height: 1.5; 
  overflow-x: auto;
  white-space: pre-wrap; 
  word-break: break-all; 
  tab-size: 2; 
  color: #24292e; 
}
</style>
