const { useState, useEffect } = React;

function App() {
    const [view, setView] = useState('login');
    const [user, setUser] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');
        if (token && storedUser) {
            setUser(JSON.parse(storedUser));
            const parsedUser = JSON.parse(storedUser);
            if (parsedUser.role === 'ADMIN') {
                setView('admin');
            } else {
                setView('dashboard');
            }
        }
    }, []);

    const navigate = (newView) => setView(newView);

    return (
        <div className="app-container">
            <div className="scanline"></div>
            {view === 'login' && <Login navigate={navigate} setUser={setUser} />}
            {view === 'register' && <Register navigate={navigate} />}
            {view === 'dashboard' && <UserDashboard user={user} navigate={navigate} />}
            {view === 'admin' && <AdminDashboard user={user} navigate={navigate} />}
        </div>
    );
}

// Helper for authenticated requests
const fetchWithAuth = async (url, options = {}) => {
    const token = localStorage.getItem('token');
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };
    return fetch(url, { ...options, headers });
};

function Login({ navigate, setUser }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleLogin = async () => {
        try {
            const res = await fetch('api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password })
            });
            const data = await res.json();
            if (res.ok) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(data.user));
                setUser(data.user);
                if (data.user.role === 'ADMIN') {
                    navigate('admin');
                } else {
                    navigate('dashboard');
                }
            } else {
                setError(data.message);
            }
        } catch (e) {
            setError('Login failed');
        }
    };

    return (
        <div className="container" style={{ maxWidth: '400px', marginTop: '10vh' }}>
            <div className="card" style={{ textAlign: 'center' }}>
                <h1 className="brand-title"><i className="fas fa-cube"></i> FinTrack 360</h1>
                <h2><i className="fas fa-lock"></i> Secure Login</h2>
                {error && <p className="text-danger">{error}</p>}
                <input type="email" placeholder="Email Address" value={email} onChange={e => setEmail(e.target.value)} />
                <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
                <button className="btn" style={{ width: '100%' }} onClick={handleLogin}>
                    <i className="fas fa-sign-in-alt"></i> Access System
                </button>
                <p style={{ textAlign: 'center', marginTop: '20px' }}>
                    <small>New User? <a href="#" style={{ color: 'var(--accent-cyan)' }} onClick={(e) => { e.preventDefault(); navigate('register') }}>Initialize Account</a></small>
                </p>
            </div>
        </div>
    );
}

function Register({ navigate }) {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('USER');
    const [error, setError] = useState('');

    const handleRegister = async () => {
        try {
            const res = await fetch('api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, password, role })
            });
            if (res.ok) {
                alert('Registration successful! Please login.');
                navigate('login');
            } else {
                const data = await res.json();
                setError(data.message);
            }
        } catch (e) {
            setError('Registration failed');
        }
    };

    return (
        <div className="container" style={{ maxWidth: '400px', marginTop: '10vh' }}>
            <div className="card" style={{ textAlign: 'center' }}>
                <h1 className="brand-title"><i className="fas fa-cube"></i> FinTrack 360</h1>
                <h2><i className="fas fa-user-plus"></i> New User Protocol</h2>
                {error && <p className="text-danger">{error}</p>}
                <input type="text" placeholder="Full Name" value={name} onChange={e => setName(e.target.value)} />
                <input type="email" placeholder="Email Address" value={email} onChange={e => setEmail(e.target.value)} />
                <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
                <select value={role} onChange={e => setRole(e.target.value)}>
                    <option value="USER">User</option>
                    <option value="ADMIN">Admin</option>
                </select>
                <button className="btn" style={{ width: '100%' }} onClick={handleRegister}>
                    <i className="fas fa-check-circle"></i> Register
                </button>
                <p style={{ textAlign: 'center', marginTop: '20px' }}>
                    <small>Existing User? <a href="#" style={{ color: 'var(--accent-cyan)' }} onClick={(e) => { e.preventDefault(); navigate('login') }}>Login</a></small>
                </p>
            </div>
        </div>
    );
}

function UserDashboard({ user, navigate }) {
    const [expenses, setExpenses] = useState([]);
    const [budgets, setBudgets] = useState([]);
    const [goals, setGoals] = useState([]);
    const [incomes, setIncomes] = useState([]);
    const [recurring, setRecurring] = useState([]);

    // Form States
    const [newExpense, setNewExpense] = useState({ amount: '', category: '', description: '', date: '' });
    const [newBudget, setNewBudget] = useState({ category: '', amount: '', startDate: '', endDate: '' });
    const [newGoal, setNewGoal] = useState({ name: '', targetAmount: '', deadline: '' });
    const [newIncome, setNewIncome] = useState({ amount: '', source: '', description: '', date: '' });
    const [newRecurring, setNewRecurring] = useState({ amount: '', category: '', description: '', frequency: 'MONTHLY', nextRunDate: '', type: 'EXPENSE' });

    useEffect(() => {
        fetchData();
    }, []);

    const fetchData = async () => {
        const expRes = await fetchWithAuth('api/finance/expenses');
        if (expRes.ok) setExpenses(await expRes.json());

        const budRes = await fetchWithAuth('api/finance/budgets');
        if (budRes.ok) setBudgets(await budRes.json());

        const goalRes = await fetchWithAuth('api/finance/goals');
        if (goalRes.ok) setGoals(await goalRes.json());

        const incRes = await fetchWithAuth('api/finance/incomes');
        if (incRes.ok) setIncomes(await incRes.json());

        const recRes = await fetchWithAuth('api/finance/recurring');
        if (recRes.ok) setRecurring(await recRes.json());
    };

    const addExpense = async () => {
        await fetchWithAuth('api/finance/expenses', {
            method: 'POST',
            body: JSON.stringify(newExpense)
        });
        setNewExpense({ amount: '', category: '', description: '', date: '' });
        fetchData();
    };

    const addBudget = async () => {
        await fetchWithAuth('api/finance/budgets', {
            method: 'POST',
            body: JSON.stringify(newBudget)
        });
        setNewBudget({ category: '', amount: '', startDate: '', endDate: '' });
        fetchData();
    };

    const addGoal = async () => {
        await fetchWithAuth('api/finance/goals', {
            method: 'POST',
            body: JSON.stringify({ ...newGoal, currentAmount: 0, status: 'IN_PROGRESS' })
        });
        setNewGoal({ name: '', targetAmount: '', deadline: '' });
        fetchData();
    };

    const addIncome = async () => {
        await fetchWithAuth('api/finance/incomes', {
            method: 'POST',
            body: JSON.stringify(newIncome)
        });
        setNewIncome({ amount: '', source: '', description: '', date: '' });
        fetchData();
    };

    const addRecurring = async () => {
        await fetchWithAuth('api/finance/recurring', {
            method: 'POST',
            body: JSON.stringify(newRecurring)
        });
        setNewRecurring({ amount: '', category: '', description: '', frequency: 'MONTHLY', nextRunDate: '', type: 'EXPENSE' });
        fetchData();
    };

    const deleteRecurring = async (id) => {
        if (confirm('Stop this recurring transaction?')) {
            await fetchWithAuth(`api/finance/recurring?id=${id}`, { method: 'DELETE' });
            fetchData();
        }
    };

    const handleLogout = async () => {
        if (confirm('Are you sure you want to logout?')) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            await fetch('api/auth/logout', { method: 'POST' });
            navigate('login');
        }
    };

    // Calculations
    const totalIncome = incomes.reduce((sum, inc) => sum + parseFloat(inc.amount), 0);
    const totalExpense = expenses.reduce((sum, exp) => sum + parseFloat(exp.amount), 0);
    const balance = totalIncome - totalExpense;

    return (
        <div className="container">
            <header className="flex-between mb-20">
                <div>
                    <h1 className="brand-title" style={{ fontSize: '2rem', marginBottom: '0' }}><i className="fas fa-cube"></i> FinTrack 360</h1>
                    <h4 className="text-muted">Welcome, Commander {user ? user.name : ''}</h4>
                </div>
                <div style={{ textAlign: 'right' }}>
                    <h3 style={{ color: balance >= 0 ? 'var(--accent-green)' : 'var(--accent-pink)', fontSize: '1.5rem' }}>
                        <i className="fas fa-wallet"></i> ${balance.toFixed(2)}
                    </h3>
                    <button className="btn btn-danger" onClick={handleLogout}><i className="fas fa-power-off"></i> Disconnect</button>
                </div>
            </header>

            {/* Quick Actions */}
            <div className="grid-2 mb-20">
                <div className="card">
                    <h3><i className="fas fa-arrow-up text-success"></i> Add Credits</h3>
                    <div className="grid-2">
                        <input type="number" placeholder="Amount" value={newIncome.amount} onChange={e => setNewIncome({ ...newIncome, amount: e.target.value })} />
                        <input type="date" value={newIncome.date} onChange={e => setNewIncome({ ...newIncome, date: e.target.value })} />
                    </div>
                    <input type="text" placeholder="Source" value={newIncome.source} onChange={e => setNewIncome({ ...newIncome, source: e.target.value })} />
                    <input type="text" placeholder="Description" value={newIncome.description} onChange={e => setNewIncome({ ...newIncome, description: e.target.value })} />
                    <button className="btn" onClick={addIncome}><i className="fas fa-plus"></i> Inject Credits</button>
                </div>

                <div className="card">
                    <h3><i className="fas fa-arrow-down text-danger"></i> Log Expense</h3>
                    <div className="grid-2">
                        <input type="number" placeholder="Amount" value={newExpense.amount} onChange={e => setNewExpense({ ...newExpense, amount: e.target.value })} />
                        <input type="date" value={newExpense.date} onChange={e => setNewExpense({ ...newExpense, date: e.target.value })} />
                    </div>
                    <input type="text" placeholder="Category" value={newExpense.category} onChange={e => setNewExpense({ ...newExpense, category: e.target.value })} />
                    <input type="text" placeholder="Description" value={newExpense.description} onChange={e => setNewExpense({ ...newExpense, description: e.target.value })} />
                    <button className="btn btn-danger" onClick={addExpense}><i className="fas fa-minus"></i> Deduct Funds</button>
                </div>
            </div>

            {/* Recent Transactions */}
            <div className="card">
                <h3><i className="fas fa-history"></i> Transaction Log</h3>
                <div className="grid-2">
                    <div>
                        <h4 className="text-success">Incoming Stream</h4>
                        <ul>
                            {incomes.map(inc => (
                                <li key={inc.id}>
                                    <span><i className="fas fa-arrow-up text-success"></i> {inc.source}</span>
                                    <span className="text-success">+${inc.amount}</span>
                                </li>
                            ))}
                        </ul>
                    </div>
                    <div>
                        <h4 className="text-danger">Outgoing Stream</h4>
                        <ul>
                            {expenses.map(exp => (
                                <li key={exp.id}>
                                    <span><i className="fas fa-arrow-down text-danger"></i> {exp.category}</span>
                                    <span className="text-danger">-${exp.amount}</span>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            </div>

            {/* Recurring Transactions Section */}
            <div className="card">
                <h3><i className="fas fa-sync-alt"></i> Automated Protocols (Recurring)</h3>
                <div className="grid-2">
                    <div>
                        <h4>Configure Protocol</h4>
                        <div className="grid-2">
                            <select value={newRecurring.type} onChange={e => setNewRecurring({ ...newRecurring, type: e.target.value })}>
                                <option value="EXPENSE">Expense</option>
                                <option value="INCOME">Income</option>
                            </select>
                            <select value={newRecurring.frequency} onChange={e => setNewRecurring({ ...newRecurring, frequency: e.target.value })}>
                                <option value="DAILY">Daily</option>
                                <option value="WEEKLY">Weekly</option>
                                <option value="MONTHLY">Monthly</option>
                            </select>
                        </div>
                        <input type="number" placeholder="Amount" value={newRecurring.amount} onChange={e => setNewRecurring({ ...newRecurring, amount: e.target.value })} />
                        <input type="text" placeholder="Category/Source" value={newRecurring.category} onChange={e => setNewRecurring({ ...newRecurring, category: e.target.value })} />
                        <input type="text" placeholder="Description" value={newRecurring.description} onChange={e => setNewRecurring({ ...newRecurring, description: e.target.value })} />
                        <input type="date" placeholder="Next Run Date" value={newRecurring.nextRunDate} onChange={e => setNewRecurring({ ...newRecurring, nextRunDate: e.target.value })} />
                        <button className="btn" onClick={addRecurring}><i className="fas fa-cog"></i> Initialize Protocol</button>
                    </div>
                    <div>
                        <h4>Active Protocols</h4>
                        <ul>
                            {recurring.map(r => (
                                <li key={r.id}>
                                    <div>
                                        <strong>{r.category}</strong>: ${r.amount} ({r.frequency}) <br />
                                        <small className="text-muted">Next: {r.nextRunDate} ({r.type})</small>
                                    </div>
                                    <button className="btn btn-danger" style={{ padding: '5px 10px', fontSize: '0.8rem' }} onClick={() => deleteRecurring(r.id)}>
                                        <i className="fas fa-stop"></i>
                                    </button>
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            </div>

            {/* Budgets & Goals */}
            <div className="grid-2 mt-20">
                <div className="card">
                    <h3><i className="fas fa-chart-pie"></i> Budget Limits</h3>
                    <div className="grid-2">
                        <input type="text" placeholder="Category" value={newBudget.category} onChange={e => setNewBudget({ ...newBudget, category: e.target.value })} />
                        <input type="number" placeholder="Limit" value={newBudget.amount} onChange={e => setNewBudget({ ...newBudget, amount: e.target.value })} />
                    </div>
                    <div className="grid-2">
                        <input type="date" placeholder="Start" value={newBudget.startDate} onChange={e => setNewBudget({ ...newBudget, startDate: e.target.value })} />
                        <input type="date" placeholder="End" value={newBudget.endDate} onChange={e => setNewBudget({ ...newBudget, endDate: e.target.value })} />
                    </div>
                    <button className="btn" onClick={addBudget}><i className="fas fa-save"></i> Set Limit</button>
                    <ul className="mt-20">
                        {budgets.map(b => (
                            <li key={b.id}>
                                <span>{b.category}</span>
                                <span>${b.amount}</span>
                            </li>
                        ))}
                    </ul>
                </div>

                <div className="card">
                    <h3><i className="fas fa-bullseye"></i> Financial Targets</h3>
                    <input type="text" placeholder="Goal Name" value={newGoal.name} onChange={e => setNewGoal({ ...newGoal, name: e.target.value })} />
                    <div className="grid-2">
                        <input type="number" placeholder="Target" value={newGoal.targetAmount} onChange={e => setNewGoal({ ...newGoal, targetAmount: e.target.value })} />
                        <input type="date" placeholder="Deadline" value={newGoal.deadline} onChange={e => setNewGoal({ ...newGoal, deadline: e.target.value })} />
                    </div>
                    <button className="btn" onClick={addGoal}><i className="fas fa-crosshairs"></i> Lock Target</button>
                    <ul className="mt-20">
                        {goals.map(g => (
                            <li key={g.id}>
                                <div>
                                    <strong>{g.name}</strong> <br />
                                    <small className="text-muted">Due: {g.deadline}</small>
                                </div>
                                <span>${g.currentAmount} / ${g.targetAmount}</span>
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
        </div>
    );
}

function AdminDashboard({ user, navigate }) {
    const [activeTab, setActiveTab] = useState('overview');
    const [logs, setLogs] = useState([]);
    const [users, setUsers] = useState([]);
    const [stats, setStats] = useState({ userCount: 0, expenseCount: 0, budgetCount: 0 });

    useEffect(() => {
        if (activeTab === 'overview') fetchStats();
        if (activeTab === 'users') fetchUsers();
        if (activeTab === 'logs') fetchLogs();
    }, [activeTab]);

    const fetchStats = async () => {
        const res = await fetchWithAuth('api/admin/stats');
        if (res.ok) setStats(await res.json());
    };

    const fetchUsers = async () => {
        const res = await fetchWithAuth('api/admin/users');
        if (res.ok) setUsers(await res.json());
    };

    const fetchLogs = async () => {
        const res = await fetchWithAuth('api/admin/logs');
        if (res.ok) setLogs(await res.json());
    };

    const deleteUser = async (id) => {
        if (confirm('Are you sure you want to delete this user?')) {
            const res = await fetchWithAuth(`api/admin/users?id=${id}`, { method: 'DELETE' });
            if (res.ok) fetchUsers();
            else alert('Failed to delete user');
        }
    };

    const handleLogout = async () => {
        if (confirm('Are you sure you want to logout?')) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            await fetch('api/auth/logout', { method: 'POST' });
            navigate('login');
        }
    };

    return (
        <div className="container">
            <header className="flex-between mb-20">
                <div>
                    <h1 className="brand-title" style={{ fontSize: '2rem', marginBottom: '0' }}><i className="fas fa-shield-alt"></i> FinTrack 360</h1>
                    <h4 className="text-muted">Admin Console // {user ? user.name : ''}</h4>
                </div>
                <button className="btn btn-danger" onClick={handleLogout}><i className="fas fa-power-off"></i> Terminate Session</button>
            </header>

            <div className="mb-20">
                <button className={`btn ${activeTab === 'overview' ? '' : 'btn-danger'}`} onClick={() => setActiveTab('overview')} style={{ marginRight: '10px' }}>
                    <i className="fas fa-tachometer-alt"></i> Overview
                </button>
                <button className={`btn ${activeTab === 'users' ? '' : 'btn-danger'}`} onClick={() => setActiveTab('users')} style={{ marginRight: '10px' }}>
                    <i className="fas fa-users"></i> Users
                </button>
                <button className={`btn ${activeTab === 'logs' ? '' : 'btn-danger'}`} onClick={() => setActiveTab('logs')}>
                    <i className="fas fa-file-code"></i> Security Logs
                </button>
            </div>

            {activeTab === 'overview' && (
                <div className="grid-3">
                    <div className="card" style={{ textAlign: 'center' }}>
                        <h3><i className="fas fa-users"></i> Total Users</h3>
                        <p style={{ fontSize: '3em', fontWeight: 'bold', color: 'var(--accent-cyan)' }}>{stats.userCount}</p>
                    </div>
                    <div className="card" style={{ textAlign: 'center' }}>
                        <h3><i className="fas fa-file-invoice-dollar"></i> Total Expenses</h3>
                        <p style={{ fontSize: '3em', fontWeight: 'bold', color: 'var(--accent-pink)' }}>{stats.expenseCount}</p>
                    </div>
                    <div className="card" style={{ textAlign: 'center' }}>
                        <h3><i className="fas fa-chart-pie"></i> Total Budgets</h3>
                        <p style={{ fontSize: '3em', fontWeight: 'bold', color: 'var(--accent-green)' }}>{stats.budgetCount}</p>
                    </div>
                </div>
            )}

            {activeTab === 'users' && (
                <div className="card">
                    <h3><i className="fas fa-users-cog"></i> User Management</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map(u => (
                                <tr key={u.id}>
                                    <td>{u.id}</td>
                                    <td>{u.name}</td>
                                    <td>{u.email}</td>
                                    <td>{u.role}</td>
                                    <td>
                                        {u.role !== 'ADMIN' && (
                                            <button className="btn btn-danger" style={{ padding: '5px 10px' }} onClick={() => deleteUser(u.id)}>
                                                <i className="fas fa-trash"></i>
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}

            {activeTab === 'logs' && (
                <div className="card">
                    <h3><i className="fas fa-terminal"></i> Security Logs</h3>
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Event</th>
                                <th>Description</th>
                                <th>IP</th>
                                <th>Time</th>
                            </tr>
                        </thead>
                        <tbody>
                            {logs.map(log => (
                                <tr key={log.id}>
                                    <td>{log.id}</td>
                                    <td style={{ color: 'var(--accent-cyan)' }}>{log.eventType}</td>
                                    <td>{log.description}</td>
                                    <td>{log.ipAddress}</td>
                                    <td>{log.timestamp}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(<App />);
