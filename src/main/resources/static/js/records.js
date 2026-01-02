

// 当DOM完全加载后执行
document.addEventListener('DOMContentLoaded', function() {
    // 初始化变量
    let currentPage = 1;
    let pageSize = 10;
    let totalPages = 0;
    let selectedRelationshipId = '';
    let selectedStatus = '';
    let relationships = []; // 存储所有亲友关系

    // 获取DOM元素
    const recordTableBody = document.getElementById('recordTableBody');
    const pagination = document.getElementById('pagination');
    const relationshipFilter = document.getElementById('relationshipFilter');
    const statusFilter = document.getElementById('statusFilter');
    const filterBtn = document.getElementById('filterBtn');
    const refreshBtn = document.getElementById('refreshBtn');

    // 初始化页面
    loadRelationships().then(() => {
        loadRecords();
    });

    // 事件监听器
    filterBtn.addEventListener('click', function() {
        selectedRelationshipId = relationshipFilter.value;
        selectedStatus = statusFilter.value;
        currentPage = 1;
        loadRecords();
    });

    statusFilter.addEventListener('change', function() {
        selectedStatus = this.value;
        currentPage = 1;
        loadRecords();
    });

    refreshBtn.addEventListener('click', function() {
        currentPage = 1;
        loadRecords();
    });

    /**
     * 加载所有亲友关系
     */
    async function loadRelationships() {
        try {
            // 获取所有亲友关系（这里假设一次性获取所有，实际可能需要分页）
            const response = await API.relationship.getList(1, 100);
            relationships = response.content;

            // 填充亲友筛选下拉框
            relationshipFilter.innerHTML = '<option value="">所有亲友</option>';

            relationships.forEach(relationship => {
                const option = document.createElement('option');
                option.value = relationship.id;
                option.textContent = relationship.name;
                relationshipFilter.appendChild(option);
            });
        } catch (error) {
            console.error('加载亲友关系失败:', error);
            alert('加载亲友关系失败，请重试');
        }
    }

    /**
     * 加载提醒记录列表
     */
    function loadRecords() {
        // 显示加载中状态
        recordTableBody.innerHTML = '<tr><td colspan="6" class="text-center">加载中...</td></tr>';

        API.reminderRecord.getList(currentPage, pageSize, selectedRelationshipId)
            .then(response => {
                // 清空表格
                recordTableBody.innerHTML = '';

                // 更新分页信息
                totalPages = response.totalPages;

                // 检查是否有数据
                if (response.content.length === 0) {
                    recordTableBody.innerHTML = '<tr><td colspan="6" class="text-center">没有找到提醒记录</td></tr>';
                    pagination.innerHTML = '';
                    return;
                }

                // 筛选状态（如果选择了状态筛选）
                let filteredRecords = response.content;
                if (selectedStatus) {
                    filteredRecords = filteredRecords.filter(record => record.status === selectedStatus);
                }

                if (filteredRecords.length === 0) {
                    recordTableBody.innerHTML = '<tr><td colspan="6" class="text-center">没有找到符合条件的提醒记录</td></tr>';
                    pagination.innerHTML = '';
                    return;
                }

                // 渲染表格数据
                filteredRecords.forEach(record => {
                    const row = document.createElement('tr');

                    // 查找亲友名称
                    const relationship = relationships.find(r => r.id === record.relationshipId);
                    const relationshipName = relationship ? relationship.name : '未知亲友';

                    // 格式化提醒日期
                    const reminderDate = record.reminderDate ? formatDate(new Date(record.reminderDate)) : '未设置';

                    // 设置提醒类型显示
                    const reminderTypeText = record.reminderType === 'BIRTHDAY' ? '生日提醒' : '自定义提醒';

                    // 设置状态样式
                    const statusClass = record.status === 'PROCESSED' ? 'bg-success' : 'bg-warning';
                    const statusText = record.status === 'PROCESSED' ? '已处理' : '待处理';

                    // 构建行内容
                    row.innerHTML = `
                        <td>${relationshipName}</td>
                        <td>${reminderTypeText}</td>
                        <td>${reminderDate}</td>
                        <td>${record.message || '无'}</td>
                        <td><span class="badge ${statusClass}">${statusText}</span></td>
                        <td>
                            ${record.status === 'PENDING' ? `
                            <button class="btn btn-sm btn-outline-success mark-btn" data-id="${record.id}">
                                <i class="fas fa-check"></i> 标记为已处理
                            </button>
                            ` : ''}
                        </td>
                    `;

                    recordTableBody.appendChild(row);
                });

                // 添加标记为已处理按钮的事件监听器
                document.querySelectorAll('.mark-btn').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const id = this.getAttribute('data-id');
                        markRecordAsProcessed(id);
                    });
                });

                // 更新分页控件
                renderPagination();
            })
            .catch(error => {
                console.error('加载提醒记录失败:', error);
                recordTableBody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">加载失败，请重试</td></tr>';
            });
    }

    /**
     * 渲染分页控件
     */
    function renderPagination() {
        pagination.innerHTML = '';

        if (totalPages <= 1) {
            return;
        }

        // 上一页按钮
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${currentPage === 1 ? 'disabled' : ''}`;
        prevLi.innerHTML = '<a class="page-link" href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a>';
        if (currentPage > 1) {
            prevLi.addEventListener('click', function() {
                currentPage--;
                loadRecords();
            });
        }
        pagination.appendChild(prevLi);

        // 页码按钮
        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, startPage + 4);

        for (let i = startPage; i <= endPage; i++) {
            const pageLi = document.createElement('li');
            pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
            pageLi.innerHTML = `<a class="page-link" href="#">${i}</a>`;

            if (i !== currentPage) {
                pageLi.addEventListener('click', function() {
                    currentPage = i;
                    loadRecords();
                });
            }

            pagination.appendChild(pageLi);
        }

        // 下一页按钮
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${currentPage === totalPages ? 'disabled' : ''}`;
        nextLi.innerHTML = '<a class="page-link" href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a>';
        if (currentPage < totalPages) {
            nextLi.addEventListener('click', function() {
                currentPage++;
                loadRecords();
            });
        }
        pagination.appendChild(nextLi);
    }

    /**
     * 标记提醒记录为已处理
     * @param {number} id - 提醒记录ID
     */
    function markRecordAsProcessed(id) {
        if (confirm('确定要将此提醒记录标记为已处理吗？')) {
            API.reminderRecord.markAsProcessed(id)
                .then(() => {
                    loadRecords();
                })
                .catch(error => {
                    console.error('标记提醒记录失败:', error);
                    alert('标记提醒记录失败，请重试');
                });
        }
    }

    /**
     * 格式化日期为 YYYY-MM-DD 字符串
     * @param {Date} date - 日期对象
     * @returns {string} - 格式化后的日期字符串
     */
    function formatDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    }
});
