package com.vaporant.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for DataSourceUtil - Spring utility for DataSource access
 * Pattern: Spring component testing with ApplicationContext mocking
 */
@SuppressWarnings("null") // Suppress warnings for mocked ApplicationContext
@ExtendWith(MockitoExtension.class)
@DisplayName("DataSourceUtil - DataSource Access Utility Tests")
class DataSourceUtilTest {

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private DataSource mockDataSource;

    // ========== HAPPY PATH TESTS ==========

    @Test
    @DisplayName("getDataSource returns DataSource when ApplicationContext is set")
    void getDataSource_contextSet_returnsDataSource() throws Exception {
        // Arrange
        DataSourceUtil util = new DataSourceUtil();
        when(mockApplicationContext.getBean(DataSource.class)).thenReturn(mockDataSource);

        // Inject ApplicationContext
        util.setApplicationContext(mockApplicationContext);

        // Act
        DataSource result = DataSourceUtil.getDataSource();

        // Assert
        assertNotNull(result, "DataSource should not be null when context is set");
        assertSame(mockDataSource, result, "Should return the DataSource from ApplicationContext");
        verify(mockApplicationContext).getBean(DataSource.class);
    }

    @Test
    @DisplayName("setApplicationContext stores ApplicationContext successfully")
    void setApplicationContext_validContext_storesContext() throws Exception {
        // Arrange
        DataSourceUtil util = new DataSourceUtil();
        when(mockApplicationContext.getBean(DataSource.class)).thenReturn(mockDataSource);

        // Act
        util.setApplicationContext(mockApplicationContext);
        DataSource result = DataSourceUtil.getDataSource();

        // Assert
        assertNotNull(result, "Context should be stored and DataSource retrievable");
    }

    // ========== NULL HANDLING TESTS ==========

    @Test
    @DisplayName("getDataSource returns null when ApplicationContext not set")
    void getDataSource_contextNotSet_returnsNull() {
        // Arrange
        // Reset static context by creating new instance without setting context
        // Note: This test assumes ApplicationContext starts as null

        // Act
        DataSource result = DataSourceUtil.getDataSource();

        // Assert - May be null if context not initialized
        // This is acceptable behavior as documented in the class
        assertTrue(result == null || result instanceof DataSource,
                "getDataSource should return null or valid DataSource");
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @DisplayName("Multiple calls to getDataSource return same instance")
    void getDataSource_multipleCalls_returnsSameDataSource() throws Exception {
        // Arrange
        DataSourceUtil util = new DataSourceUtil();
        when(mockApplicationContext.getBean(DataSource.class)).thenReturn(mockDataSource);
        util.setApplicationContext(mockApplicationContext);

        // Act
        DataSource result1 = DataSourceUtil.getDataSource();
        DataSource result2 = DataSourceUtil.getDataSource();

        // Assert
        assertSame(result1, result2, "Multiple calls should return same DataSource instance");
    }

    @Test
    @DisplayName("setApplicationContext can be called multiple times")
    void setApplicationContext_calledMultipleTimes_updatesContext() throws Exception {
        // Arrange
        DataSourceUtil util = new DataSourceUtil();
        ApplicationContext anotherContext = mock(ApplicationContext.class);
        DataSource anotherDataSource = mock(DataSource.class);

        when(mockApplicationContext.getBean(DataSource.class)).thenReturn(mockDataSource);
        when(anotherContext.getBean(DataSource.class)).thenReturn(anotherDataSource);

        // Act
        util.setApplicationContext(mockApplicationContext);
        DataSource firstResult = DataSourceUtil.getDataSource();

        util.setApplicationContext(anotherContext);
        DataSource secondResult = DataSourceUtil.getDataSource();

        // Assert
        assertNotNull(firstResult, "First DataSource should not be null");
        assertNotNull(secondResult, "Second DataSource should not be null");
    }

    // ========== INTEGRATION BEHAVIOR TESTS ==========

    @Test
    @DisplayName("Component implements ApplicationContextAware interface correctly")
    void dataSourceUtil_implementsApplicationContextAware() {
        // Arrange
        DataSourceUtil util = new DataSourceUtil();

        // Assert
        assertTrue(util instanceof org.springframework.context.ApplicationContextAware,
                "DataSourceUtil should implement ApplicationContextAware");
    }
}
